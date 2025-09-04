package com.dcd.server.core.domain.volume.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.volume.exception.VolumeDeleteFailureException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.service.DeleteVolumeService
import org.springframework.stereotype.Service

@Service
class DeleteDockerVolumeServiceImpl(
    private val commandPort: CommandPort
) : DeleteVolumeService {
    override fun deleteVolume(volume: Volume) {
        val exitValue = commandPort.executeShellCommand("docker volume rm ${volume.volumeName}")

        if (exitValue != 0)
            throw VolumeDeleteFailureException()
    }
}