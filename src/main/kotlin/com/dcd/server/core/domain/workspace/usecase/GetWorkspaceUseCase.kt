package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@ReadOnlyUseCase
class GetWorkspaceUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(workspaceId: String): WorkspaceResDto {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace)
        return WorkspaceResDto(
            id = workspace.id,
            title = workspace.title,
            description = workspace.description,
            owner = workspace.owner.toDto(),
            applicationList = applicationList.map { it.toDto() }
        )
    }
}