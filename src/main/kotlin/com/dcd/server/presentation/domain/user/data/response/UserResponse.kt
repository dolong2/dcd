package com.dcd.server.presentation.domain.user.data.response

import com.dcd.server.core.domain.user.model.Status

data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val status: Status
)
