package com.dcd.server.presentation.domain.volume.data.response

import java.util.UUID

data class VolumeSimpleResponse(
    val id: UUID,
    val name: String,
    val description: String
)
