package com.dcd.server.core.domain.volume.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.service.CopyVolumeService
import org.springframework.stereotype.Service

@Service
class CopyDockerVolumeServiceImpl(
    private val commandPort: CommandPort
) : CopyVolumeService {
    override fun copyVolumeContent(existingVolume: Volume, newVolume: Volume) {
        val volumeCopyCmd =
            """
                docker run --rm -it \
                    -v ${existingVolume.volumeName}:/from \
                    -v ${newVolume.volumeName}:/to \
                    alpine ash -c \"cp -a /from/. /to/\"
            """.trimIndent()
        commandPort.executeShellCommand(volumeCopyCmd)
    }
}