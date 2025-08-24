package com.dcd.server.core.domain.auth.dto.request

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

data class EmailSendReqDto(
    val email: String,
    val usage: EmailAuthUsage
)