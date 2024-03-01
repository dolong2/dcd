package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.ContainerNotCreatedException
import com.dcd.server.core.domain.application.exception.ContainerNotRunException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.DockerRunService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service

@Service
class DockerRunServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : DockerRunService {
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