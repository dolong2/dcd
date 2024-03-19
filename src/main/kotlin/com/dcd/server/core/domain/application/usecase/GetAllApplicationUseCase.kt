package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@ReadOnlyUseCase
class GetAllApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val queryWorkspacePort: QueryWorkspacePort
) {
    @WorkspaceOwnerVerification
    fun execute(workspaceId: String): ApplicationListResDto {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        return ApplicationListResDto(
            queryApplicationPort
                .findAllByWorkspace(
                    workspace
                )
                .map { it.toDto() }
        )
    }
}