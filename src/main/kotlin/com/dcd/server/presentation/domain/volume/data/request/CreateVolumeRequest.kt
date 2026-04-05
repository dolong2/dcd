package com.dcd.server.presentation.domain.volume.data.request

import com.dcd.server.core.domain.volume.model.enums.VolumeSizeUnit
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class CreateVolumeRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_.\\s-]{0,62}$")
    val name: String,
    @field:NotBlank
    val description: String,
    val size: Long? = null,
    val sizeUnit: VolumeSizeUnit? = null
)
