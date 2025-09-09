package com.dcd.server.core.domain.volume.dto.response

import com.dcd.server.core.domain.application.dto.response.ApplicationProfileResDto

data class VolumeMountResDto(
    val mountPath: String,
    val readOnly: Boolean,
    val applicationInfo: ApplicationProfileResDto
)
