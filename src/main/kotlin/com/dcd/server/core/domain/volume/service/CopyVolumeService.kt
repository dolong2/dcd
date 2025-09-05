package com.dcd.server.core.domain.volume.service

import com.dcd.server.core.domain.volume.model.Volume

interface CopyVolumeService {
    fun copyVolumeContent(existingVolume: Volume, newVolume: Volume)
}