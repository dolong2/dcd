package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@UseCase
class DeleteApplicationUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val commandApplicationPort: CommandApplicationPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    fun execute(id: String) {
        val user = getCurrentUserService.getCurrentUser()
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        validateWorkspaceOwnerService.validateOwner(user, application.workspace)
        commandApplicationPort.delete(application)
    }
}