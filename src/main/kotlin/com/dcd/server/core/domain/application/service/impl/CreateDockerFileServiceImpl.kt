package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.cmd.FileContent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateDockerFileService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class CreateDockerFileServiceImpl(
    private val queryApplicationPort: QueryApplicationPort
) : CreateDockerFileService {
    override fun createFileByApplicationId(id: String, javaVersion: Int) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        when(application.applicationType){
            ApplicationType.SPRING_BOOT -> {
                val name = application.name
                try {
                    val file = File("./$name/Dockerfile")
                    file.writeText(FileContent.getSpringBootDockerFileContent(name, javaVersion))
                        if (!file.createNewFile())
                            return
                } catch (e: IOException) {
                    throw ApplicationNotFoundException()
                }
            }
        }
    }

    override fun createFileToApplication(application: Application, javaVersion: Int) {
        when(application.applicationType){
            ApplicationType.SPRING_BOOT -> {
                val name = application.name
                val file = File("./$name/Dockerfile")
                file.writeText(FileContent.getSpringBootDockerFileContent(name, javaVersion))
                try {
                    if (!file.createNewFile())
                        return
                } catch (e: IOException) {
                    throw ApplicationNotFoundException()
                }
            }
        }
    }
}