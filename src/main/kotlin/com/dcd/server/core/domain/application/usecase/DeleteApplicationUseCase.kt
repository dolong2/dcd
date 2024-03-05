package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeleteApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.service.DeleteImageService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@UseCase
class DeleteApplicationUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val commandApplicationPort: CommandApplicationPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val deleteContainerService: DeleteContainerService,
    private val deleteImageService: DeleteImageService
) {
    fun execute(id: String) {
        val user = getCurrentUserService.getCurrentUser()
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        validateWorkspaceOwnerService.validateOwner(user, application.workspace)

        if (application.status == ApplicationStatus.RUNNING)
            throw CanNotDeleteApplicationException()

        deleteContainerService.deleteContainer(application)
        deleteImageService.deleteImage(application)

        commandApplicationPort.delete(application)
    }
}