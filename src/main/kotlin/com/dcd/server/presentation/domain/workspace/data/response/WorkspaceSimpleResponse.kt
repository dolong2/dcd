package com.dcd.server.presentation.domain.workspace.data.response

data class WorkspaceSimpleResponse(
    val id: String,
    val title: String,
    val description: String,
    val applicationList: List<WorkspaceApplicationResponse>
)
