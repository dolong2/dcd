package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.EmailSendRequestData
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest

fun EmailSendRequest.toDto(): EmailSendRequestData =
    EmailSendRequestData(
        email = this.email
    )