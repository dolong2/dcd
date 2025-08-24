package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase(readOnly = true)
class GetAllApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val queryApplicationEnvPort: QueryApplicationEnvPort
) {
    fun execute(labels: List<String>?): ListResDto<ApplicationResDto> {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()

        return ListResDto(
            queryApplicationPort
                .findAllByWorkspace(workspace, labels)
                .map {
                    val applicationEnvList = queryApplicationEnvPort.findByApplication(it)
                    it.toDto(applicationEnvList)
                }
        )
    }
}