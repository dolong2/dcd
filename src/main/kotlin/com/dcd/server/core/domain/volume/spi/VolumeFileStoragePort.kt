package com.dcd.server.core.domain.volume.spi

import com.dcd.server.core.domain.volume.model.Volume
import java.nio.file.Path

interface VolumeFileStoragePort {
    fun resolveVolumeRootPath(volume: Volume): Path

    fun resolveTargetPath(volume: Volume, relativePath: String): Path
}
