package com.dcd.server.core.domain.user.dto.request

data class PasswordChangeReqDto(
    val existingPassword: String,
    val newPassword: String
)
