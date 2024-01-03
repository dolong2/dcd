package com.dcd.server.core.domain.workspace.dto.response

import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.user.dto.response.UserResDto

data class WorkspaceResDto(
    val id: String,
    val title: String,
    val description: String,
    val applicationList: List<ApplicationResDto>,
    val owner: UserResDto
)
