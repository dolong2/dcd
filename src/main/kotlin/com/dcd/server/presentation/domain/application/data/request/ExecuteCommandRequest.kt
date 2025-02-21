package com.dcd.server.presentation.domain.application.data.request

import jakarta.validation.constraints.NotBlank

data class ExecuteCommandRequest(
    @field:NotBlank
    val command: String
)