package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class DeleteGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val getCurrentUserService: GetCurrentUserService,
    private val commandWorkspacePort: CommandWorkspacePort
) {
    fun execute(workspaceId: String, key: String) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())
        val currentUser = getCurrentUserService.getCurrentUser()

        if (workspace.owner.id != currentUser.id)
            throw WorkspaceOwnerNotSameException()

        val updatedGlobalEnv = workspace.globalEnv.toMutableMap()
        updatedGlobalEnv.remove(key)

        val updatedWorkspace = workspace.copy(globalEnv = updatedGlobalEnv)
        commandWorkspacePort.save(updatedWorkspace)
    }
}