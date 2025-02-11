package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.BuildDockerImageService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.application.util.FailureCase
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
        val directoryName = application.name
        withContext(Dispatchers.IO) {
            val exitValue = when (application.applicationType) {
                ApplicationType.SPRING_BOOT -> {
                    commandPort.executeShellCommand("cd ./$directoryName && ./gradlew clean build")
                        .run {
                            if (this == 0)
                                commandPort.executeShellCommand("cd ./$directoryName && docker build -t ${application.containerName}:latest .")
                            else this
                        }
                }

                else -> {
                    commandPort.executeShellCommand("cd ./$directoryName && docker build -t ${application.containerName}:latest .")
                }
            }
            if (exitValue != 0)
                commandPort.executeShellCommand("rm -rf $directoryName")
            checkExitValuePort.checkApplicationExitValue(exitValue, application, this, FailureCase.IMAGE_BUILD_FAILURE)
        }
    }

    override suspend fun buildImageByApplication(application: Application) {
        val directoryName = application.name
        withContext(Dispatchers.IO) {
            val exitValue = when(application.applicationType) {
                ApplicationType.SPRING_BOOT -> {
                    commandPort.executeShellCommand("cd ./$directoryName && ./gradlew clean build")
                    commandPort.executeShellCommand("cd ./$directoryName && docker build -t ${application.containerName}:latest .")
                }
                else -> {
                    commandPort.executeShellCommand("cd ./$directoryName && docker build -t ${application.containerName}:latest .")
                }
            }
            if (exitValue != 0)
                commandPort.executeShellCommand("rm -rf $directoryName")
            checkExitValuePort.checkApplicationExitValue(exitValue, application, this, FailureCase.IMAGE_BUILD_FAILURE)
        }
    }

}