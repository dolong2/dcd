package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toEntity
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class CreateApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    fun execute(workspaceId: String, createApplicationReqDto: CreateApplicationReqDto) {
        val workspace = queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException()
        val currentUser = getCurrentUserService.getCurrentUser()
        validateWorkspaceOwnerService.validateOwner(currentUser, workspace)
        val application = createApplicationReqDto.toEntity(workspace)
        commandApplicationPort.save(application)
    }
}