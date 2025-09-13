package com.dcd.server.presentation.domain.volume.data.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class MountVolumeRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^(?!(?:/(?:proc|sys|dev|etc)?$))/(?:(?:[A-Za-z0-9._-]+/)*[A-Za-z0-9._-]+)?$")
    val mountPath: String,
    val readOnly: Boolean,
)
