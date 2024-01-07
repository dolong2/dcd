package com.dcd.server.presentation.domain.auth.data.request

data class NonAuthChangePasswordRequest(
    val email: String,
    val newPassword: String
)
