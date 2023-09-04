package com.dcd.server.presentation.domain.auth.data.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SignUpRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!~₩*)(_><,./-]).{6,30}\$")
    val password: String,
    @field:NotBlank
    val name: String,
)