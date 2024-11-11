package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.extension.toEntity
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.response.CreateWorkspaceResDto
import com.dcd.server.core.domain.workspace.service.CreateNetworkService
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort

@UseCase
class CreateWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val currentUserService: GetCurrentUserService,
    private val createNetworkService: CreateNetworkService
) {
    fun execute(createWorkspaceReqDto: CreateWorkspaceReqDto): CreateWorkspaceResDto {
        val currentUser = currentUserService.getCurrentUser()

        val workspace = createWorkspaceReqDto.toEntity(currentUser)
        commandWorkspacePort.save(workspace)

        createNetworkService.createNetwork(createWorkspaceReqDto.title)

        return CreateWorkspaceResDto(workspace.id)
    }
}