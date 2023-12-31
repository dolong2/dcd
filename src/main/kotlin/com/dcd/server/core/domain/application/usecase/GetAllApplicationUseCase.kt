package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResponseDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@ReadOnlyUseCase
class GetAllApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort
) {
    fun execute(workspaceId: String): ApplicationListResponseDto {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())
        val currentUser = getCurrentUserService.getCurrentUser()
        if (workspace.owner.equals(currentUser).not())
            throw WorkspaceOwnerNotSameException()
        return ApplicationListResponseDto(
            queryApplicationPort
                .findAllByWorkspace(
                    workspace
                )
                .map { it.toDto() }
        )
    }
}