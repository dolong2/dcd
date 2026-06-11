package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.InitialScriptService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.spi.ApplicationImageFilePort
import com.dcd.server.core.domain.application.spi.ApplicationRemoteRepoPort
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher

@UseCase
class UpdateApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val applicationRemoteRepoPort: ApplicationRemoteRepoPort,
    private val applicationImageFilePort: ApplicationImageFilePort,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val queryVolumePort: QueryVolumePort,
    private val containerPort: ContainerPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val initialScriptService: InitialScriptService
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    @Lock("#id")
    fun execute(id: String, updateApplicationReqDto: UpdateApplicationReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        val updatedApplication =
            application.copy(
                name = updateApplicationReqDto.name,
                description = updateApplicationReqDto.description,
                applicationType = updateApplicationReqDto.applicationType,
                githubUrl = updateApplicationReqDto.githubUrl,
                version = updateApplicationReqDto.version,
                port = updateApplicationReqDto.port
            )
        commandApplicationPort.save(updatedApplication)

        initialScriptService.write(updatedApplication, updateApplicationReqDto.initialScripts)

        if (application.name != updateApplicationReqDto.name) {
            launch {
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.PENDING, updatedApplication))

                // 이름이 변경된 애플리케이션의 이미지및, 컨테이너 생성
                val applicationType = updatedApplication.applicationType
                when(applicationType) {
                    ApplicationType.SPRING_BOOT, ApplicationType.NEST_JS, ApplicationType.GIN -> {
                        applicationRemoteRepoPort.cloneApplicationRemoteRepo(updatedApplication)
                    }
                    else -> {}
                }

                applicationImageFilePort.createImageFile(updatedApplication)

                containerPort.execute {
                    buildImage(updatedApplication)
                    val volumeMounts = queryVolumePort.findAllMountByApplication(updatedApplication)
                    createContainer(updatedApplication, volumeMounts)
                }

                deleteApplicationDirectoryService.deleteApplicationDirectory(updatedApplication)

                // 이름이 변경되기 전 애플리케이션의 이미지및, 컨테이너 제거
                containerPort.execute {
                    deleteContainer(application)
                    deleteImage(application)
                }

                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.STOPPED, updatedApplication))
            }
        }
    }
}