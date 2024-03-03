package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteImageService
import org.springframework.stereotype.Service

@Service
class DeleteImageServiceImpl(
    private val commandPort: CommandPort
) : DeleteImageService {
    override fun deleteImage(application: Application) {
        commandPort.executeShellCommand("docker rmi ${application.name.lowercase()}")
    }
}