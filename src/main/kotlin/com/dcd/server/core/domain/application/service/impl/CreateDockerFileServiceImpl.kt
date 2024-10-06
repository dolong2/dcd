package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.NotSupportedTypeException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateDockerFileService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
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
    private val commandPort: CommandPort,
    private val checkExitValuePort: CheckExitValuePort,
    private val eventPublisher: ApplicationEventPublisher
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
        val name = application.name
        val mutableEnv = application.env.toMutableMap()
        mutableEnv.putAll(application.workspace.globalEnv)

        commandPort.executeShellCommand("mkdir $name")
            .also {exitValue ->
                checkExitValuePort.checkApplicationExitValue(exitValue, application, coroutineScope)
            }

        val file = File("./$name/Dockerfile")
        val fileContent = when (application.applicationType) {
            ApplicationType.SPRING_BOOT ->
                FileContent.getSpringBootDockerFileContent(name, version, application.port, mutableEnv)

            ApplicationType.MYSQL ->
                FileContent.getMYSQLDockerFileContent(version, application.port, mutableEnv)

            ApplicationType.MARIA_DB ->
                FileContent.getMARIADBDockerFileContent(version, application.port, mutableEnv)

            ApplicationType.REDIS ->
                FileContent.getRedisDockerFileContent(version, application.port, mutableEnv)

            ApplicationType.NEST_JS ->
                FileContent.getNestJsDockerFileContent(version, application.port, mutableEnv)
        }
        file.writeText(fileContent)
        try {
            if (!file.createNewFile())
                return
        } catch (e: IOException) {
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application))
        }
    }
}