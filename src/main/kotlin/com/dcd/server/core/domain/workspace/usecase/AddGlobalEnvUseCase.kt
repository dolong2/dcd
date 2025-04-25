package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.workspace.dto.request.AddGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class AddGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    @Lock("#workspaceId")
    fun execute(workspaceId: String, addGlobalEnvReqDto: AddGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val updatedGlobalEnv = workspace.globalEnv.associate { it.key to it.value }.toMutableMap()
        updatedGlobalEnv.putAll(addGlobalEnvReqDto.envList)

        val updatedWorkspace = workspace.copy(globalEnv = updatedGlobalEnv.map { GlobalEnv(key = it.key, value = it.value, encryption = false) })
        commandWorkspacePort.save(updatedWorkspace)
    }
}