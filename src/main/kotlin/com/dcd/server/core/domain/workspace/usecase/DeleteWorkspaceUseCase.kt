package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class DeleteWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val getCurrentUserService: GetCurrentUserService
) {
    fun execute(workspaceId: String) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())
        val currentUser = getCurrentUserService.getCurrentUser()
        if (currentUser.equals(workspace.owner).not())
            throw WorkspaceOwnerNotSameException()
        commandWorkspacePort.delete(workspace)
    }
}