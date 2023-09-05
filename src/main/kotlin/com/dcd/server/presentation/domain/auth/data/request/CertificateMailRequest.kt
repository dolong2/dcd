package com.dcd.server.presentation.domain.auth.data.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CertificateMailRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val code: String
)