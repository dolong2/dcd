package com.dcd.server.core.domain.volume.dto.response

import java.util.UUID

data class VolumeSimpleResDto(
    val id: UUID,
    val name: String,
    val description: String
)
