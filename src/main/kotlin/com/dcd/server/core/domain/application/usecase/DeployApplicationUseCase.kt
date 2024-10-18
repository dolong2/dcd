package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeployApplicationException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher

@UseCase
class DeployApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteImageService: DeleteImageService,
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val eventPublisher: ApplicationEventPublisher,
    private val workspaceInfo: WorkspaceInfo
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val deploymentChannel = Channel<Application>(capacity = Channel.UNLIMITED)
    init {
        repeat(3) {
            launch {
                for (application in deploymentChannel) {
                    deployApplication(application)
                }
            }
        }
    }

    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING || application.status == ApplicationStatus.PENDING)
            throw CanNotDeployApplicationException()

        val success = deploymentChannel.trySend(application).isSuccess

        if (success)
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, application))
        else
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application))
    }

    fun execute(labels: List<String>) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        applicationList.forEach {
            // 만약 애플리케이션의 상태가 배포할 수 없는 상태일때는 건너뜀
            if (it.status == ApplicationStatus.RUNNING || it.status == ApplicationStatus.PENDING)
                return@forEach

            val success = deploymentChannel.trySend(it).isSuccess
            if (success)
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, it))
            else
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, it))
        }
    }

    private suspend fun deployApplication(application: Application) {
        deleteContainerService.deleteContainer(application)
        deleteImageService.deleteImage(application)

        val version = application.version
        val externalPort = application.externalPort

        val applicationType = application.applicationType
        when(applicationType) {
            ApplicationType.SPRING_BOOT -> {
                cloneApplicationByUrlService.cloneByApplication(application)
                modifyGradleService.modifyGradleByApplication(application)
            }
            ApplicationType.NEST_JS -> {
                cloneApplicationByUrlService.cloneByApplication(application)
            }
            else -> {}
        }

        createDockerFileService.createFileToApplication(application, version)
        buildDockerImageService.buildImageByApplication(application)
        createContainerService.createContainer(application, externalPort)

        deleteApplicationDirectoryService.deleteApplicationDirectory(application)
        eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.STOPPED, application))
    }
}