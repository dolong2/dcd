package com.dcd.server.presentation.domain.user.data.request

data class PasswordChangeRequest(
    val existingPassword: String,
    val newPassword: String
)
