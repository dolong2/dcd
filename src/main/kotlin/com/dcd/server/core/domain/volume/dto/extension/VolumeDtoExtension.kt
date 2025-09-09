package com.dcd.server.core.domain.volume.dto.extension

import com.dcd.server.core.domain.application.dto.extenstion.toProfileDto
import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.dto.request.UpdateVolumeReqDto
import com.dcd.server.core.domain.volume.dto.response.VolumeDetailResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeMountResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeSimpleResDto
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

fun CreateVolumeReqDto.toEntity(workspace: Workspace): Volume =
    Volume(
        id = UUID.randomUUID(),
        name = this.name,
        description = this.description,
        workspace = workspace,
    )

fun UpdateVolumeReqDto.toEntity(volume: Volume): Volume =
    Volume(
        id = volume.id,
        name = this.name,
        description = this.description,
        workspace = volume.workspace,
    )

fun Volume.toResDto(): VolumeSimpleResDto =
    VolumeSimpleResDto(
        id = this.id,
        name = this.name,
        description = this.description
    )

fun Volume.toDetailResDto(volumeMountList: List<VolumeMount>): VolumeDetailResDto =
    VolumeDetailResDto(
        id = this.id,
        name = this.name,
        description = this.description,
        mountList = volumeMountList.map { it.toResDto() }
    )

fun VolumeMount.toResDto(): VolumeMountResDto =
    VolumeMountResDto(
        mountPath = this.mountPath,
        readOnly = this.readOnly,
        applicationInfo = this.application.toProfileDto()
    )