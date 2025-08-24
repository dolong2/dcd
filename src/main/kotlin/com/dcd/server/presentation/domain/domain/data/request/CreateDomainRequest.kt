package com.dcd.server.presentation.domain.domain.data.request

import jakarta.validation.constraints.NotBlank

data class CreateDomainRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String
)
