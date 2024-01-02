package com.dcd.server.core.domain.workspace.dto.response

import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResponseDto

data class WorkspaceProfileResDto(
    val id: String,
    val title: String,
    val applicationList: List<ApplicationProfileResponseDto>
)