package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class UpdateWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    fun execute(workspaceId: String, updateWorkspaceReqDto: UpdateWorkspaceReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val updatedWorkspace = workspace.copy(title = updateWorkspaceReqDto.title, description = updateWorkspaceReqDto.description)
        commandWorkspacePort.save(updatedWorkspace)
    }
}