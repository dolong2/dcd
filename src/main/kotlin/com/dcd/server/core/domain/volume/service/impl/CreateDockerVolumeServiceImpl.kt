package com.dcd.server.core.domain.volume.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.volume.exception.VolumeCreationFailureException
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.service.CreateVolumeService
import org.springframework.stereotype.Service

@Service
class CreateDockerVolumeServiceImpl(
    private val commandPort: CommandPort
) : CreateVolumeService {
    override fun create(volume: Volume) {
        StringBuilder().apply {
            append("docker volume create")
            volume.size?.let { append(" --opt size=${it}${volume.sizeUnit?.symbol ?: "b"}") }
            append(" ${volume.volumeName}")
        }.toString().also { command ->
            val commandResult = commandPort.executeShellCommand(command)
            if (commandResult.exitValue != 0)
                throw VolumeCreationFailureException()
        }
    }
}