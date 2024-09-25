package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeleteApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.service.DeleteImageService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@UseCase
class DeleteApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteImageService: DeleteImageService
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING || application.status == ApplicationStatus.PENDING)
            throw CanNotDeleteApplicationException()

        launch {
            deleteContainerService.deleteContainer(application)
            deleteImageService.deleteImage(application)
        }

        commandApplicationPort.delete(application)
    }
}