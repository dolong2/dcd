package com.dcd.server.core.domain.auth.dto.request

data class SignInRequestDto(
    val email: String,
    val password: String
)