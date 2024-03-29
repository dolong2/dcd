package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.ContainerNotRunException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.RunContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service

@Service
class RunContainerServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : RunContainerService {
    override fun runApplication(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        run(application)
    }

    override fun runApplication(application: Application) {
        run(application)
    }

    private fun run(application: Application) {
        val exitValue = commandPort.executeShellCommand("docker start ${application.name.lowercase()}")
        if (exitValue != 0) throw ContainerNotRunException()
    }
}