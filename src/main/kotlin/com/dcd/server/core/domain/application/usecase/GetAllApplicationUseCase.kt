package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResponseDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@ReadOnlyUseCase
class GetAllApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort
) {
    fun execute(workspaceId: String): ApplicationListResponseDto =
        ApplicationListResponseDto(
            queryApplicationPort
                .findAllByWorkspace(
                    queryWorkspacePort.findById(workspaceId)
                        ?: throw WorkspaceNotFoundException()
                )
                .map { it.toDto() }
        )
}