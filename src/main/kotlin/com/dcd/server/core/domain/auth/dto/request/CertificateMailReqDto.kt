package com.dcd.server.core.domain.auth.dto.request

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

data class CertificateMailReqDto (
    val email: String,
    val code: String,
    val usage: EmailAuthUsage
)