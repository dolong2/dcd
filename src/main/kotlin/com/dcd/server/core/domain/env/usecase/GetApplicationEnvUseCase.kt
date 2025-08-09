package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.env.dto.extension.toSimpleResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvListResDto
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase(readOnly = true)
class GetApplicationEnvUseCase(
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(): ApplicationEnvListResDto {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationEnvList = queryApplicationEnvPort.findAllByWorkspace(workspace)
        return ApplicationEnvListResDto(applicationEnvList.map { it.toSimpleResDto() })
    }
}