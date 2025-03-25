package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class DeleteGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    @Lock("#workspaceId+#key")
    fun execute(workspaceId: String, key: String) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        if (workspace.globalEnv.contains(key).not())
            throw GlobalEnvNotFoundException()

        val updatedGlobalEnv = workspace.globalEnv.toMutableMap()
        updatedGlobalEnv.remove(key)

        val updatedWorkspace = workspace.copy(globalEnv = updatedGlobalEnv)
        commandWorkspacePort.save(updatedWorkspace)
    }
}