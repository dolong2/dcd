package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ContainerNotStoppedException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.StopContainerService
import org.springframework.stereotype.Service

@Service
class StopContainerServiceImpl(
    private val commandPort: CommandPort
) : StopContainerService {
    override fun stopContainer(application: Application) {
        val exitValue = commandPort.executeShellCommand("docker stop ${application.name.lowercase()}")
        if (exitValue != 0) throw ContainerNotStoppedException()
    }
}