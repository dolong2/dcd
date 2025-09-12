package com.dcd.server.presentation.domain.volume.data.request

import jakarta.validation.constraints.NotBlank

data class MountVolumeRequest(
    @field:NotBlank
    val mountPath: String,
    val readOnly: Boolean,
)
