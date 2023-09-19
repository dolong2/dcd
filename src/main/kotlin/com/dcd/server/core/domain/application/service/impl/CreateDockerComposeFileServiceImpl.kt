package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.enums.DBType
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateDockerComposeFileService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class CreateDockerComposeFileServiceImpl(
    private val queryApplicationPort: QueryApplicationPort
) : CreateDockerComposeFileService {
    override fun createDockerComposeYml(id: String, dbTypes: Array<DBType>, rootPassword: String, dataBaseName: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        when(application.applicationType) {
            ApplicationType.SPRING_BOOT -> {
                val name = application.name
                var content = FileContent.getServerComposeContent()
                content += FileContent.getApplicationComposeContent(name, 8080)
                dbTypes.forEach {
                    when(it) {
                        DBType.MYSQL -> content += FileContent.getMySqlDockerComposeContent(rootPassword, dataBaseName)
                        DBType.REDIS -> content += FileContent.getRedisComposeContent()
                        DBType.MARIADB -> content += FileContent.getMariaDBComposeContent(rootPassword, dataBaseName)
                    }
                }
                try {
                    val file = File("./$name/docker-compose.yml")
                    file.writeText(content)
                    if (!file.createNewFile())
                        return
                } catch (e: IOException) {
                    throw ApplicationNotFoundException()
                }
            }
        }
    }

    override fun createDockerComposeYml(application: Application, dbTypes: Array<DBType>, rootPassword: String, dataBaseName: String) {
        when(application.applicationType) {
            ApplicationType.SPRING_BOOT -> {
                val name = application.name
                var content = FileContent.getServerComposeContent()
                content += FileContent.getApplicationComposeContent(name, 8080)
                dbTypes.forEach {
                    when(it) {
                        DBType.MYSQL -> content += FileContent.getMySqlDockerComposeContent(rootPassword, dataBaseName)
                        DBType.REDIS -> content += FileContent.getRedisComposeContent()
                        DBType.MARIADB -> content += FileContent.getMariaDBComposeContent(rootPassword, dataBaseName)
                    }
                }
                try {
                    val file = File("./$name/docker-compose.yml")
                    file.writeText(content)
                    if (!file.createNewFile())
                        return
                } catch (e: IOException) {
                    throw ApplicationNotFoundException()
                }
            }
        }
    }
}