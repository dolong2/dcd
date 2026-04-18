package com.dcd.server.presentation.domain.domain.data.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateDomainRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?$")
    val name: String,
    @field:NotBlank
    val description: String
)
