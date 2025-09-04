package com.dcd.server.core.domain.volume.service

import com.dcd.server.core.domain.volume.model.Volume

interface DeleteVolumeService {
    fun deleteVolume(volume: Volume)
}