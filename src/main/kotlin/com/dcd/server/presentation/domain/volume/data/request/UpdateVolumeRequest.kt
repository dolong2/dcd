package com.dcd.server.presentation.domain.volume.data.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UpdateVolumeRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_.\\s-]{0,62}$")
    val name: String,
    @field:NotBlank
    val description: String
)
