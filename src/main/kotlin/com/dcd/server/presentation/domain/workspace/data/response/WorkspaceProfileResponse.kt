package com.dcd.server.presentation.domain.workspace.data.response

import com.dcd.server.presentation.domain.application.data.response.ApplicationProfileResponse

data class WorkspaceProfileResponse(
    val id: String,
    val title: String,
    val applicationList: List<ApplicationProfileResponse>
)