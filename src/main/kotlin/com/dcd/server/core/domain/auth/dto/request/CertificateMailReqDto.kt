package com.dcd.server.core.domain.auth.dto.request

data class CertificateMailReqDto (
    val email: String,
    val code: String
)