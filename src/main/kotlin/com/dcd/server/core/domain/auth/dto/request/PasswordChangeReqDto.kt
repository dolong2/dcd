package com.dcd.server.core.domain.auth.dto.request

data class PasswordChangeReqDto(
    val existingPassword: String,
    val newPassword: String
)
