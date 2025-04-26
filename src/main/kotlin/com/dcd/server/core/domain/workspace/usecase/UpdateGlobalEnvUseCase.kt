package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.env.repository.GlobalEnvRepository

@UseCase
class UpdateGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val commandGlobalEnvPort: CommandGlobalEnvPort,
    private val queryGlobalEnvPort: QueryGlobalEnvPort,
    private val globalEnvRepository: GlobalEnvRepository
) {
    @Lock("#workspaceId+#envKey")
    fun execute(workspaceId: String, envKey: String, updateGlobalEnvReqDto: UpdateGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val findAll = globalEnvRepository.findAll()
        findAll.forEach {
            println("it = ${it.key} ${it.value}")
        }

        val globalEnv = (queryGlobalEnvPort.findByKeyAndWorkspace(envKey, workspace)
            ?: throw GlobalEnvNotFoundException())
        globalEnv.value = updateGlobalEnvReqDto.newValue
        commandGlobalEnvPort.save(globalEnv, workspace)
    }
}