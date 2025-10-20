package com.dcd.server.presentation.domain.volume.data.extension

import com.dcd.server.core.domain.volume.dto.response.VolumeDetailResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeListResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeMountResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeSimpleResDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.volume.data.response.VolumeDetailResponse
import com.dcd.server.presentation.domain.volume.data.response.VolumeListResponse
import com.dcd.server.presentation.domain.volume.data.response.VolumeMountResponse
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

fun VolumeMountResDto.toResponse(): VolumeMountResponse =
    VolumeMountResponse(
        mountPath = this.mountPath,
        readOnly = this.readOnly,
        applicationInfo = this.applicationInfo.toResponse()
    )

fun VolumeDetailResDto.toResponse(): VolumeDetailResponse =
    VolumeDetailResponse(
        id = this.id,
        name = this.name,
        description = this.description,
        mountList = this.mountList.map { it.toResponse() }
    )