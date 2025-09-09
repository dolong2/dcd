package com.dcd.server.presentation.domain.volume.data.response

import com.dcd.server.presentation.domain.application.data.response.ApplicationProfileResponse

data class VolumeMountResponse(
    val mountPath: String,
    val readOnly: Boolean,
    val applicationInfo: ApplicationProfileResponse
)
