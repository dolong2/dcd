package com.dcd.server.core.domain.volume.spi

import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount

interface CommandVolumePort {
    fun save(volume: Volume)

    fun delete(volume: Volume)

    fun saveMount(volumeMount: VolumeMount)
}