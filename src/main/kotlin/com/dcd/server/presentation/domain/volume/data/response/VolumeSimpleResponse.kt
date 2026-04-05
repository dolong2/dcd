package com.dcd.server.presentation.domain.volume.data.response

import com.dcd.server.core.domain.volume.model.enums.VolumeSizeUnit
import java.util.UUID

data class VolumeSimpleResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val size: Long?,
    val sizeUnit: VolumeSizeUnit?
)
