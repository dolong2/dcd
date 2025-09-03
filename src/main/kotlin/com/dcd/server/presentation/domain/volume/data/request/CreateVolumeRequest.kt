package com.dcd.server.presentation.domain.volume.data.request

import jakarta.validation.constraints.NotBlank

data class CreateVolumeRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String
)
