package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.BuildDockerImageService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service

@Service
class BuildDockerImageServiceImpl(
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort
) : BuildDockerImageService {
    override fun buildImageByApplicationId(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val name = application.name
        commandPort.executeShellCommand("cd ./$name && ./gradlew clean build")
        commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:v0.0.1 .")
    }

    override fun buildImageByApplication(application: Application) {
        val name = application.name
        commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:v0.0.1")
    }

}