package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.exception.HttpConfigFailureException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.GenerateHttpConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GenerateHttpConfigServiceImpl(
    private val commandPort: CommandPort,
    @Value("\${domain.config-path:.}")
    private val domainConfigPath: String
) : GenerateHttpConfigService {

    override fun generateWebServerConfig(application: Application, domain: String) {
        val webServerConfig = FileContent.getApplicationHttpConfig(application, domain)
        val httpConfigDirectory = "${domainConfigPath}/nginx/conf/${application.workspace.id}"
        val exitValue = commandPort.executeShellCommand(
            "mkdir -p $httpConfigDirectory && " +
            "cat <<EOF > ${httpConfigDirectory}/${application.name.replace(" ", "-")}-http.conf \n ${webServerConfig}EOF"
        )

        if (exitValue != 0)
            throw HttpConfigFailureException()
    }
}