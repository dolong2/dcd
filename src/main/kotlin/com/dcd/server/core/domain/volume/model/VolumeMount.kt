package com.dcd.server.core.domain.volume.model

import com.dcd.server.core.domain.application.model.Application
import java.util.UUID

class VolumeMount(
    val id: UUID,
    val application: Application,
    val volume: Volume,
    val mountPath: String,
    val readOnly: Boolean
)