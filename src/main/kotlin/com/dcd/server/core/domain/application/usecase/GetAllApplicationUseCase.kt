package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@ReadOnlyUseCase
class GetAllApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(labels: List<String>?): ApplicationListResDto {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()

        return ApplicationListResDto(
            queryApplicationPort
                .findAllByWorkspace(workspace, labels)
                .map { it.toDto() }
        )
    }
}