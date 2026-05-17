package com.dcd.server.core.domain.domain.service.impl

import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.domain.exception.HttpConfigFailureException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.domain.service.GenerateHttpConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class GenerateHttpConfigServiceImpl(
    private val fileOperationPort: FileOperationPort,
    @Value("\${domain.config-path:.}")
    private val domainConfigPath: String
) : GenerateHttpConfigService {

    override fun generateWebServerConfig(application: Application, domain: Domain) {
        val webServerConfig = FileContent.getApplicationHttpConfig(application, domain.getDomainName())
        val configDirectory = Paths.get(domainConfigPath, "nginx", "conf", domain.id)
        val configFileName = "${application.name.replace(" ", "-")}-http.conf"
        val configFilePath = configDirectory.resolve(configFileName)

        try {
            fileOperationPort.createDirectory(configDirectory)
            fileOperationPort.writeFile(configFilePath, webServerConfig)
        } catch (e: FileOperationException) {
            throw HttpConfigFailureException()
        }
    }
}