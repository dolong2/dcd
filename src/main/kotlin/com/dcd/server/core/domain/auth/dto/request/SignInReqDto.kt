package com.dcd.server.core.domain.auth.dto.request

data class SignInReqDto(
    val email: String,
    val password: String
)