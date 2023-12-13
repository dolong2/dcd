package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.extension.toDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceListResDto
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@ReadOnlyUseCase
class GetAllWorkspaceUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(): WorkspaceListResDto {
        val user = getCurrentUserService.getCurrentUser()
        val workspaceDtoList = queryWorkspacePort.findByUser(user).map {
            val applicationList = queryApplicationPort.findAllByWorkspace(it)
            it.toDto(applicationList)
        }
        return WorkspaceListResDto(workspaceDtoList)
    }
}