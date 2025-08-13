package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.env.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.env.dto.extension.toResDto
import com.dcd.server.core.domain.env.dto.extension.toSimpleResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvListResDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvResDto
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import java.util.UUID

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

    fun execute(envId: UUID): ApplicationEnvResDto {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationEnv = (queryApplicationEnvPort.findById(envId)
            ?: throw ApplicationEnvNotFoundException())

        if (workspace != applicationEnv.workspace)
            throw ApplicationEnvNotFoundException()

        return applicationEnv.toResDto()
    }
}