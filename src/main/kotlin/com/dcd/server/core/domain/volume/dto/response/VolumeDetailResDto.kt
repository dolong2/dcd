package com.dcd.server.core.domain.volume.dto.response

import com.dcd.server.core.domain.volume.model.enums.VolumeSizeUnit
import java.util.UUID

data class VolumeDetailResDto(
    val id: UUID,
    val name: String,
    val description: String,
    val size: Long?,
    val sizeUnit: VolumeSizeUnit?,
    val mountList: List<VolumeMountResDto>
)
