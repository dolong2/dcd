package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.extension.toEntity
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.response.CreateWorkspaceResDto
import com.dcd.server.core.domain.workspace.exception.AlreadyExistsWorkspaceException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class CreateWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val currentUserService: GetCurrentUserService,
    private val containerPort: ContainerPort
) {
    fun execute(createWorkspaceReqDto: CreateWorkspaceReqDto): CreateWorkspaceResDto {
        val currentUser = currentUserService.getCurrentUser()

        if (queryWorkspacePort.existsByTitle(createWorkspaceReqDto.title))
            throw AlreadyExistsWorkspaceException()

        val workspace = createWorkspaceReqDto.toEntity(currentUser)
        commandWorkspacePort.save(workspace)

        containerPort.execute { createNetwork(workspace) }

        return CreateWorkspaceResDto(workspace.id)
    }
}