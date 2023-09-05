package com.dcd.server.core.domain.auth.dto.request

data class CertificateMailRequestDto (
    val email: String,
    val code: String
)