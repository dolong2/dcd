package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.extension.toSimpleDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceSimpleResDto
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@ReadOnlyUseCase
class GetAllWorkspaceUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(): ListResDto<WorkspaceSimpleResDto> {
        val user = getCurrentUserService.getCurrentUser()
        val workspaceDtoList = queryWorkspacePort.findByUser(user).map {
            val applicationList = queryApplicationPort.findAllByWorkspace(it)
            it.toSimpleDto(applicationList)
        }
        return ListResDto(workspaceDtoList)
    }
}