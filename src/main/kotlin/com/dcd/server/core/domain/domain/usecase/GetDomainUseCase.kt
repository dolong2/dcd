package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.domain.dto.extension.toResDto
import com.dcd.server.core.domain.domain.dto.response.DomainResDto
import com.dcd.server.core.domain.domain.spi.QueryDomainPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase(readOnly = true)
class GetDomainUseCase(
    private val queryDomainPort: QueryDomainPort,
    private val workspaceInfo: WorkspaceInfo,
) {
    fun execute(): ListResDto<DomainResDto> {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val domainListResDto =
            queryDomainPort.findByWorkspace(workspace)
                .map { it.toResDto() }
                .let { ListResDto(it) }
        return domainListResDto
    }
}