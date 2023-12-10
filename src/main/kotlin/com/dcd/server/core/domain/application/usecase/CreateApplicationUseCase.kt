package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.application.dto.extenstion.toEntity
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class CreateApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val securityService: SecurityService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryUserPort: QueryUserPort
) {
    fun execute(workspaceId: String, createApplicationReqDto: CreateApplicationReqDto) {
        val workspace = queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException()
        val application = createApplicationReqDto.toEntity(workspace)
        commandApplicationPort.save(application)
    }
}