package com.dcd.server.core.domain.volume.model

import com.dcd.server.core.domain.application.model.Application
import java.util.Objects
import java.util.UUID

class VolumeMount(
    val application: Application,
    val volume: Volume,
    val mountPath: String,
    val readOnly: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (other !is VolumeMount) return false
        return this.application == other.application && this.volume == other.volume
    }

    override fun hashCode(): Int {
        return Objects.hash(application, volume)
    }
}