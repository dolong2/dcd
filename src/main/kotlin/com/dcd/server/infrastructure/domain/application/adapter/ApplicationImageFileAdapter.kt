package com.dcd.server.infrastructure.domain.application.adapter

import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.common.spi.EncryptPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.ApplicationImageFilePort
import com.dcd.server.core.domain.application.spi.QueryApplicationInitialScriptPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.application.util.FailureCase
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.infrastructure.domain.application.file.ImageFileContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.nio.file.Paths

@Component
class ApplicationImageFileAdapter(
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val queryApplicationInitialScriptPort: QueryApplicationInitialScriptPort,
    private val fileOperationPort: FileOperationPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val encryptPort: EncryptPort
) : ApplicationImageFilePort {
    override suspend fun createImageFile(application: Application) {
        withContext(Dispatchers.IO) {
            createFile(application, this)
        }
    }

    private fun createFile(application: Application, coroutineScope: CoroutineScope) {
        val version = application.version
        val applicationPath = Paths.get(application.name)
        val applicationEnv =
            queryApplicationEnvPort.findByApplication(application)
                .flatMap { it.details }
                .associate {
                    if (it.encryption)
                        it.key to encryptPort.decrypt(it.value)
                    else
                        it.key to it.value
                }

        val initialScripts =
            queryApplicationInitialScriptPort
                .findAllByApplication(application)
                .map { it.script }

        try {
            fileOperationPort.createDirectory(applicationPath)
        } catch (e: FileOperationException) {
            try {
                fileOperationPort.deleteDirectory(applicationPath)
            } catch (ignored: FileOperationException) {
            }
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, FailureCase.CREATE_DIRECTORY_FAILURE))
            coroutineScope.cancel()
            return
        }

        val fileContent =
            ImageFileContent.getApplicationDockerFileContent(
                application.applicationType,
                version,
                application.port,
                applicationEnv,
                initialScripts
            )

        try {
            fileOperationPort.writeFile(Paths.get("${applicationPath}", "Dockerfile"), fileContent)
        } catch (e: FileOperationException) {
            try {
                fileOperationPort.deleteDirectory(applicationPath)
            } catch (ignored: FileOperationException) {
            }
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, FailureCase.CREATE_DOCKER_FILE_FAILURE))
            coroutineScope.cancel()
        }
    }
}
