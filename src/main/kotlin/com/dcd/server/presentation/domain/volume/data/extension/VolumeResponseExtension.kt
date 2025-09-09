package com.dcd.server.presentation.domain.volume.data.extension

import com.dcd.server.core.domain.volume.dto.response.VolumeListResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeSimpleResDto
import com.dcd.server.presentation.domain.volume.data.response.VolumeListResponse
import com.dcd.server.presentation.domain.volume.data.response.VolumeSimpleResponse

fun VolumeSimpleResDto.toResponse(): VolumeSimpleResponse =
    VolumeSimpleResponse(
        id = this.id,
        name = this.name,
        description = this.description
    )

fun VolumeListResDto.toResponse(): VolumeListResponse =
    VolumeListResponse(
        list = this.list.map { it.toResponse() }
    )