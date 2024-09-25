package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.ImageNotBuiltException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.BuildDockerImageService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class BuildDockerImageServiceImpl(
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val eventPublisher: ApplicationEventPublisher
) : BuildDockerImageService {
    override suspend fun buildImageByApplicationId(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val name = application.name
        withContext(Dispatchers.IO) {
            val exitValue = when (application.applicationType) {
                ApplicationType.SPRING_BOOT -> {
                    commandPort.executeShellCommand("cd ./$name && ./gradlew clean build")
                    commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:latest .")
                }

                else -> {
                    commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:latest .")
                }
            }
            if (exitValue != 0) eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application))
        }
    }

    override suspend fun buildImageByApplication(application: Application) {
        val name = application.name
        withContext(Dispatchers.IO) {
            val exitValue = when(application.applicationType) {
                ApplicationType.SPRING_BOOT -> {
                    commandPort.executeShellCommand("cd ./$name && ./gradlew clean build")
                    commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:latest .")
                }
                else -> {
                    commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:latest .")
                }
            }
            if (exitValue != 0) eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application))
        }
    }

}