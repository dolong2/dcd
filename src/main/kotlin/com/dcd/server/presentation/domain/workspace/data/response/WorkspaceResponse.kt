package com.dcd.server.presentation.domain.workspace.data.response

import com.dcd.server.presentation.domain.application.data.response.ApplicationResponse
import com.dcd.server.presentation.domain.user.data.response.UserResponse

data class WorkspaceResponse(
    val id: String,
    val title: String,
    val description: String,
    val applicationList: List<WorkspaceApplicationResponse>,
    val owner: UserResponse
)
