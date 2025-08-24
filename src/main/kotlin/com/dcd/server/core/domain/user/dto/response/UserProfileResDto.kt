package com.dcd.server.core.domain.user.dto.response

import com.dcd.server.core.domain.workspace.dto.response.WorkspaceProfileResDto

data class UserProfileResDto(
    val user: UserResDto,
    val workspaces: List<WorkspaceProfileResDto>
)