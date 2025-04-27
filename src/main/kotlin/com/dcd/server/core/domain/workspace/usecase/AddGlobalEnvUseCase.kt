package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.workspace.dto.request.AddGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class AddGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandGlobalEnvPort: CommandGlobalEnvPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val queryGlobalEnvPort: QueryGlobalEnvPort
) {
    @Lock("#workspaceId")
    fun execute(workspaceId: String, addGlobalEnvReqDto: AddGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val globalEnvList = addGlobalEnvReqDto.envList.map { addEnv ->
            queryGlobalEnvPort.findByKeyAndWorkspace(addEnv.key, workspace)
                ?.let {
                    GlobalEnv(
                        id = it.id,
                        key = addEnv.key,
                        value = addEnv.value,
                        encryption = false
                    )
                }
                ?: GlobalEnv(
                    key = addEnv.key,
                    value = addEnv.value,
                    encryption = false
                )
        }

        commandGlobalEnvPort.saveAll(globalEnvList, workspace)
    }
}