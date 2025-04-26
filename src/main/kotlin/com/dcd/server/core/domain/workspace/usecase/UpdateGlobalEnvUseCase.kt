package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class UpdateGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val commandGlobalEnvPort: CommandGlobalEnvPort
) {
    @Lock("#workspaceId+#envKey")
    fun execute(workspaceId: String, envKey: String, updateGlobalEnvReqDto: UpdateGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val globalEnv = (workspace.globalEnv.find { it.key == envKey }
            ?: throw GlobalEnvNotFoundException())
        globalEnv.value = updateGlobalEnvReqDto.newValue
        commandGlobalEnvPort.save(globalEnv, workspace)
    }
}