package com.dcd.server.presentation.domain.user.data.exetension

import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.presentation.domain.user.data.response.UserResponse

fun UserResDto.toResponse(): UserResponse =
    UserResponse(
        id = this.id,
        email = this.email,
        name = this.name
    )