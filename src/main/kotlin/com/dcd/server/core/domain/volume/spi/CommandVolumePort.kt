package com.dcd.server.core.domain.volume.spi

import com.dcd.server.core.domain.volume.model.Volume

interface CommandVolumePort {
    fun save(volume: Volume)

    fun delete(volume: Volume)
}