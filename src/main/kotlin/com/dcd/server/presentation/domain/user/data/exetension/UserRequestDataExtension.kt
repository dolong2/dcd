package com.dcd.server.presentation.domain.user.data.exetension

import com.dcd.server.core.domain.user.dto.request.PasswordChangeReqDto
import com.dcd.server.presentation.domain.user.data.request.PasswordChangeRequest

fun PasswordChangeRequest.toDto(): PasswordChangeReqDto =
    PasswordChangeReqDto(
        existingPassword = this.existingPassword,
        newPassword = this.newPassword
    )