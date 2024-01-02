package com.dcd.server.presentation.domain.user.data.response

import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceProfileResponse

data class UserProfileResponse(
    val user: UserResponse,
    val workspaces: List<WorkspaceProfileResponse>
)
