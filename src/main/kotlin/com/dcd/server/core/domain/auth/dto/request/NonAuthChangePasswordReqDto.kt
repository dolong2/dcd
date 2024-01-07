package com.dcd.server.core.domain.auth.dto.request

data class NonAuthChangePasswordReqDto(
    val email: String,
    val newPassword: String
)
