package com.dcd.server.core.domain.volume.service

import com.dcd.server.core.domain.volume.model.Volume

interface CreateVolumeService {
    fun create(volume: Volume)
}