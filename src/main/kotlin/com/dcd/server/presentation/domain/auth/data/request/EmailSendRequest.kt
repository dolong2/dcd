package com.dcd.server.presentation.domain.auth.data.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class EmailSendRequest(
    @field:NotBlank
    @field:Email
    val email: String
)