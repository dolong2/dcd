package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.common.spi.LockPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeployApplicationException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.spi.ApplicationRemoteRepoPort
import com.dcd.server.core.domain.application.spi.ApplicationImageFilePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.springframework.context.ApplicationEventPublisher

@UseCase
class DeployApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val containerPort: ContainerPort,
    private val queryVolumePort: QueryVolumePort,
    private val applicationRemoteRepoPort: ApplicationRemoteRepoPort,
    private val applicationImageFilePort: ApplicationImageFilePort,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val lockPort: LockPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val workspaceInfo: WorkspaceInfo
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING || application.status == ApplicationStatus.PENDING)
            throw CanNotDeployApplicationException()

        launch {
            deployApplication(application)
        }

        eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, application))
    }

    fun execute(labels: List<String>) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)
            .filter { it.status != ApplicationStatus.RUNNING && it.status != ApplicationStatus.PENDING }

        if(applicationList.isEmpty())
            return

        val deploymentChannel = Channel<Application>(capacity = Channel.UNLIMITED)
        applicationList.forEach {
            //락 적용
            lockPort.lock(it.id, 1000 * 10 * 3, 1000 * 10 * 6) {
                // 배포 작업을 큐에 추가
                deploymentChannel.trySend(it).isSuccess
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, it))
            };
        }
        deploymentChannel.close()

        // 코루틴을 생성하여 작업 처리
        val jobs = (1..3).map {
            launch {
                for (application in deploymentChannel) {
                    deployApplication(application)
                }
            }
        }

        // 작업 완료 후 코루틴 스코프 종료
        launch {
            jobs.joinAll()
        }
    }

    private suspend fun deployApplication(application: Application) {
        containerPort.execute {
            deleteContainer(application)
            deleteImage(application)

            runBlocking {
                val applicationType = application.applicationType
                when(applicationType) {
                    ApplicationType.SPRING_BOOT, ApplicationType.NEST_JS, ApplicationType.GIN -> {
                        applicationRemoteRepoPort.cloneApplicationRemoteRepo(application)
                    }
                    else -> {}
                }
                applicationImageFilePort.createImageFile(application)
            }

            buildImage(application)
            val volumeMounts = queryVolumePort.findAllMountByApplication(application)
            createContainer(application, volumeMounts)

            runBlocking {
                deleteApplicationDirectoryService.deleteApplicationDirectory(application)
            }
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.STOPPED, application))
        }
    }
}