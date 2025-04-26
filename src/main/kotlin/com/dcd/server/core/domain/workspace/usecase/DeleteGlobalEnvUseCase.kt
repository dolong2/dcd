package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class DeleteGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandGlobalEnvPort: CommandGlobalEnvPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val queryGlobalEnvPort: QueryGlobalEnvPort
) {
    @Lock("#workspaceId+#key")
    fun execute(workspaceId: String, key: String) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val globalEnv = (queryGlobalEnvPort.findByKeyAndWorkspace(key, workspace)
            ?: throw GlobalEnvNotFoundException())
        commandGlobalEnvPort.delete(globalEnv)
    }
}