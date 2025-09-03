package com.dcd.server.core.domain.volume.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.volume.exception.VolumeCreationFailedException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.service.CreateVolumeService
import org.springframework.stereotype.Service

@Service
class CreateDockerVolumeServiceImpl(
    private val commandPort: CommandPort
) : CreateVolumeService {
    override fun create(volume: Volume) {
        val exitValue = commandPort.executeShellCommand("docker volume create ${volume.volumeName}")

        if (exitValue != 0)
            throw VolumeCreationFailedException()
    }
}