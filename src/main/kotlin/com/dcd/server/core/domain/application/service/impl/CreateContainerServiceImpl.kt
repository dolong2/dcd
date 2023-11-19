package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.CreateContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service
import java.lang.StringBuilder

@Service
class CreateContainerServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : CreateContainerService {
    override fun createContainer(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        commandPort.executeShellCommand("cd ${application.name} && docker-compose up -d")
    }

    override fun createContainer(application: Application) {
        commandPort.executeShellCommand("cd ${application.name} && docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d ${application.name.lowercase()}")
    }

    override fun createContainer(application: Application, env: Map<String, String>) {
        val envString = StringBuilder()
        env.forEach {
            envString.append("-e ${it.key}=${it.value}")
        }
        commandPort.executeShellCommand("cd ${application.name} && docker run $envString --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d ${application.name.lowercase()}")
    }
}