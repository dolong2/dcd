package com.dcd.server.core.domain.volume.dto.request

data class MountVolumeReqDto(
    val mountPath: String,
    val readOnly: Boolean,
)
