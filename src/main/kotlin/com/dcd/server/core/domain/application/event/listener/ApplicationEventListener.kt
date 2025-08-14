package com.dcd.server.core.domain.application.event.listener

import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.event.DeployApplicationEvent
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.BuildDockerImageService
import com.dcd.server.core.domain.application.service.CloneApplicationByUrlService
import com.dcd.server.core.domain.application.service.CreateContainerService
import com.dcd.server.core.domain.application.service.CreateDockerFileService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.service.DeleteImageService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ApplicationEventListener(
    private val commandApplicationPort: CommandApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteImageService: DeleteImageService,
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val queryApplicationPort: QueryApplicationPort
) {
    @EventListener
    @Transactional(rollbackFor = [Exception::class])
    fun process(event: ChangeApplicationStatusEvent) {
        val updatedApplication = event.application.copy(
            status = event.status,
            failureReason = event.failureCase?.reason
        )

        commandApplicationPort.save(updatedApplication)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun process(event: DeployApplicationEvent) {
        val applicationList = queryApplicationPort.findByIds(event.applicationIdList)

        applicationList.forEach { application ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    deleteContainerService.deleteContainer(application)
                    deleteImageService.deleteImage(application)

                    val version = application.version
                    val externalPort = application.externalPort

                    val applicationType = application.applicationType
                    when (applicationType) {
                        ApplicationType.SPRING_BOOT, ApplicationType.NEST_JS -> {
                            cloneApplicationByUrlService.cloneByApplication(application)
                        }

                        else -> {}
                    }

                    createDockerFileService.createFileToApplication(application, version)
                    buildDockerImageService.buildImageByApplication(application)
                    createContainerService.createContainer(application, externalPort)
                    deleteApplicationDirectoryService.deleteApplicationDirectory(application)

                    val updatedApplication = application.copy(status = ApplicationStatus.STOPPED)
                    commandApplicationPort.save(updatedApplication)
                } catch (e: Exception) {
                    val updatedApplication = application.copy(status = ApplicationStatus.FAILURE, failureReason = e.message)
                    commandApplicationPort.save(updatedApplication)
                }
            }

        }
    }
}