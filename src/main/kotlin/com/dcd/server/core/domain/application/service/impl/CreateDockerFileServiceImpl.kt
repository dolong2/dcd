package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.common.spi.EncryptPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateDockerFileService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.application.util.FailureCase
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class CreateDockerFileServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val commandPort: CommandPort,
    private val checkExitValuePort: CheckExitValuePort,
    private val eventPublisher: ApplicationEventPublisher,
    private val encryptPort: EncryptPort
) : CreateDockerFileService {
    override suspend fun createFileByApplicationId(id: String, version: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        withContext(Dispatchers.IO) {
            createFile(application, version, this)
        }
    }

    override suspend fun createFileToApplication(application: Application, version: String) {
        withContext(Dispatchers.IO) {
            createFile(application, version, this)
        }
    }

    private fun createFile(application: Application, version: String, coroutineScope: CoroutineScope) {
        val directoryName = "'${application.name}'"
        val applicationEnv =
            queryApplicationEnvPort.findByApplication(application)
                .flatMap { it.details }
                .associate {
                    if (it.encryption)
                        it.key to encryptPort.decrypt(it.value)
                    else
                        it.key to it.value
                }.toMutableMap()

        val globalEnv = application.workspace.globalEnv
            .flatMap { it.details }
            .associate {
                if (it.encryption)
                    it.key to encryptPort.decrypt(it.value)
                else
                    it.key to it.value
            }
        applicationEnv.putAll(globalEnv)

        commandPort.executeShellCommand("mkdir -p $directoryName")
            .also {exitValue ->
                if (exitValue != 0)
                    commandPort.executeShellCommand("rm -rf $directoryName")
                checkExitValuePort.checkApplicationExitValue(exitValue, application, coroutineScope, FailureCase.CREATE_DIRECTORY_FAILURE)
            }

        val file = File("./${application.name}/Dockerfile")
        val fileContent = when (application.applicationType) {
            ApplicationType.SPRING_BOOT ->
                FileContent.getSpringBootDockerFileContent(version, application.port, applicationEnv)

            ApplicationType.MYSQL ->
                FileContent.getMYSQLDockerFileContent(version, application.port, applicationEnv)

            ApplicationType.MARIA_DB ->
                FileContent.getMARIADBDockerFileContent(version, application.port, applicationEnv)

            ApplicationType.REDIS ->
                FileContent.getRedisDockerFileContent(version, application.port, applicationEnv)

            ApplicationType.NEST_JS ->
                FileContent.getNestJsDockerFileContent(version, application.port, applicationEnv)
        }
        try {
            if (!file.exists())
                file.createNewFile()
            file.writeText(fileContent)
        } catch (e: IOException) {
            commandPort.executeShellCommand("rm -rf $directoryName")
            coroutineScope.cancel()
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, FailureCase.CREATE_DOCKER_FILE_FAILURE))
        }
    }
}