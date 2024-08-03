package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class UpdateGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort
) {
    fun execute(workspaceId: String, envKey: String, updateGlobalEnvReqDto: UpdateGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        val mutableEnv = workspace.globalEnv.toMutableMap()
        if (mutableEnv.containsKey(envKey).not())
            throw GlobalEnvNotFoundException()

        mutableEnv[envKey] = updateGlobalEnvReqDto.newValue

        val newWorkspace = workspace.copy(globalEnv = mutableEnv)
        commandWorkspacePort.save(newWorkspace)
    }
}