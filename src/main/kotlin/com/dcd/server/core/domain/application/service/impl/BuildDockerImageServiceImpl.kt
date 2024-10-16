package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.BuildDockerImageService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class BuildDockerImageServiceImpl(
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val checkExitValuePort: CheckExitValuePort
) : BuildDockerImageService {
    override suspend fun buildImageByApplicationId(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val name = application.name
        withContext(Dispatchers.IO) {
            val exitValue = when (application.applicationType) {
                ApplicationType.SPRING_BOOT -> {
                    commandPort.executeShellCommand("cd ./$name && ./gradlew clean build")
                        .run {
                            if (this == 0)
                                commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:latest .")
                            else this
                        }
                }

                else -> {
                    commandPort.executeShellCommand("cd ./$name && docker build -t ${name.lowercase()}:latest .")
                }
            }
            checkExitValuePort.checkApplicationExitValue(exitValue, application, this)
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
            checkExitValuePort.checkApplicationExitValue(exitValue, application, this)
        }
    }

}