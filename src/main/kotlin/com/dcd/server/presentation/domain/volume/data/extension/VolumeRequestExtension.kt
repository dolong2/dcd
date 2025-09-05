package com.dcd.server.presentation.domain.volume.data.extension

import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.dto.request.UpdateVolumeReqDto
import com.dcd.server.presentation.domain.volume.data.request.CreateVolumeRequest
import com.dcd.server.presentation.domain.volume.data.request.UpdateVolumeRequest

fun CreateVolumeRequest.toDto(): CreateVolumeReqDto =
    CreateVolumeReqDto(
        name = this.name,
        description = this.description
    )

fun UpdateVolumeRequest.toDto(): UpdateVolumeReqDto =
    UpdateVolumeReqDto(
        name = this.name,
        description = this.description
    )