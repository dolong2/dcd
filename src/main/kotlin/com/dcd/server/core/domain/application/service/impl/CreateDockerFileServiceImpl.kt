package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.NotSupportedTypeException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateDockerFileService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class CreateDockerFileServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : CreateDockerFileService {
    override fun createFileByApplicationId(id: String, version: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        createFile(application, version)
    }

    override fun createFileToApplication(application: Application, version: String) {
        createFile(application, version)
    }

    private fun createFile(application: Application, version: String) {
        val name = application.name
        val mutableEnv = application.env.toMutableMap()
        mutableEnv.putAll(application.workspace.globalEnv)

        commandPort.executeShellCommand("mkdir $name")

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
            throw ApplicationNotFoundException()
        }
    }
}