package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.service.DeleteImageService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.ApplicationEventPublisher

@UseCase
class UpdateApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteImageService: DeleteImageService,
    private val eventPublisher: ApplicationEventPublisher
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(id: String, updateApplicationReqDto: UpdateApplicationReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        if (application.name != updateApplicationReqDto.name) {
            launch {
                deleteContainerService.deleteContainer(application)
                deleteImageService.deleteImage(application)
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.STOPPED, application))
            }
        }

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
    }
}