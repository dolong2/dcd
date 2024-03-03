package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ContainerNotCreatedException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateContainerService
import org.springframework.stereotype.Service

@Service
class CreateContainerServiceImpl(
    private val commandPort: CommandPort
) : CreateContainerService{
    override fun createContainer(application: Application, externalPort: Int) {
        create(application, externalPort = externalPort)
    }

    override fun createContainer(application: Application, version: String, externalPort: Int) {
        create(application, version, externalPort)
    }

    private fun create(application: Application, version: String = "latest", externalPort: Int) {
        val cmd =
            "docker create --network ${application.workspace.title.replace(' ', '_')} " +
            "--name ${application.name.lowercase()} -d " +
            "-p ${externalPort}:${application.port} ${application.name.lowercase()}:$version"

        val exitValue = commandPort.executeShellCommand(cmd)
        if (exitValue != 0) throw ContainerNotCreatedException()
    }
}