package com.dcd.server.core.domain.workspace.dto.response

import com.dcd.server.core.domain.user.dto.response.UserResDto

data class WorkspaceResDto(
    val id: String,
    val title: String,
    val description: String,
    val applicationList: List<WorkspaceApplicationResDto>,
    val owner: UserResDto
)
