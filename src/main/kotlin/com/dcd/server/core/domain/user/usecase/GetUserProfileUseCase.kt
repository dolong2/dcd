package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toProfileDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.dto.extension.toProfileDto
import com.dcd.server.core.domain.user.dto.response.UserProfileResDto
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.extension.toProfileDto
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class GetUserProfileUseCase(
    private val getCurrentUserService: GetCurrentUserService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(): UserProfileResDto {
        val user = getCurrentUserService.getCurrentUser()
        val workspaceList = queryWorkspacePort.findByUser(user)
        val workspaceProfileRes = workspaceList
            .map { workspace ->
                val applicationList = queryApplicationPort
                    .findAllByWorkspace(workspace)
                    .map { it.toProfileDto() }
                workspace.toProfileDto(applicationList)
            }
        return user.toDto().toProfileDto(workspaceProfileRes)
    }
}