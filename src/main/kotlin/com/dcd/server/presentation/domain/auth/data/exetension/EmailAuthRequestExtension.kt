package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.EmailSendRequestDto
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest

fun EmailSendRequest.toDto(): EmailSendRequestDto =
    EmailSendRequestDto(
        email = this.email
    )