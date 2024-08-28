package com.dcd.server.core.domain.user.dto.response

import com.dcd.server.core.domain.user.model.Status

data class UserResDto(
    val id: String,
    val email: String,
    val name: String,
    val status: Status
)
