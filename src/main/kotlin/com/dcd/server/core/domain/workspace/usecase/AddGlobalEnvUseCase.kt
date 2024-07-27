package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.request.AddGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class AddGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val getCurrentUserService: GetCurrentUserService,
    private val commandWorkspacePort: CommandWorkspacePort
) {
    fun execute(workspaceId: String, addGlobalEnvReqDto: AddGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())
        val currentUser = getCurrentUserService.getCurrentUser()

        if (workspace.owner.id != currentUser.id)
            throw WorkspaceOwnerNotSameException()

        val updatedGlobalEnv = workspace.globalEnv.toMutableMap()
        updatedGlobalEnv.putAll(addGlobalEnvReqDto.envList)

        val updatedWorkspace = workspace.copy(globalEnv = updatedGlobalEnv)
        commandWorkspacePort.save(updatedWorkspace)
    }
}