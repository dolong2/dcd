package com.dcd.server.presentation.domain.auth.data.request

import jakarta.validation.constraints.Pattern

data class NonAuthChangePasswordRequest(
    val email: String,
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[@#\$%^&+=!~₩*)(_><,./-]).{6,30}\$")
    val newPassword: String
)
