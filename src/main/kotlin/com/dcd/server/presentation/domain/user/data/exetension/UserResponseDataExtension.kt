package com.dcd.server.presentation.domain.user.data.exetension

import com.dcd.server.core.domain.user.dto.response.UserProfileResDto
import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.presentation.domain.user.data.response.UserProfileResponse
import com.dcd.server.presentation.domain.user.data.response.UserResponse
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse

fun UserResDto.toResponse(): UserResponse =
    UserResponse(
        id = this.id,
        email = this.email,
        name = this.name,
        status = this.status
    )

fun UserProfileResDto.toResponse(): UserProfileResponse =
    UserProfileResponse(
        user = this.user.toResponse(),
        workspaces = this.workspaces.map { it.toResponse() }
    )