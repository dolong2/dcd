package com.dcd.server.core.domain.auth.dto.request

data class SignUpRequestDto (
    val email: String,
    val password: String,
    val name: String
)