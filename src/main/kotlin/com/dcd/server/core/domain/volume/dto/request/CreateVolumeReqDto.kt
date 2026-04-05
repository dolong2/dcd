package com.dcd.server.core.domain.volume.dto.request

import com.dcd.server.core.domain.volume.model.enums.VolumeSizeUnit

data class CreateVolumeReqDto(
    val name: String,
    val description: String,
    val size: Long? = null,
    val sizeUnit: VolumeSizeUnit? = null
)
