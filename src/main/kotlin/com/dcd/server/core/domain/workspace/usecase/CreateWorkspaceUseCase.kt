package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.extension.toEntity
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.service.CreateNetworkService
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort

@UseCase
class CreateWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val currentUserService: GetCurrentUserService,
    private val createNetworkService: CreateNetworkService
) {
    fun execute(createWorkspaceReqDto: CreateWorkspaceReqDto) {
        val currentUser = currentUserService.getCurrentUser()
        commandWorkspacePort.save(createWorkspaceReqDto.toEntity(currentUser))
        createNetworkService.createNetwork(createWorkspaceReqDto.title)
    }
}