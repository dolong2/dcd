package com.dcd.server.presentation.domain.volume.data.extension

import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.presentation.domain.volume.data.request.CreateVolumeRequest

fun CreateVolumeRequest.toDto(): CreateVolumeReqDto =
    CreateVolumeReqDto(
        name = this.name,
        description = this.description
    )