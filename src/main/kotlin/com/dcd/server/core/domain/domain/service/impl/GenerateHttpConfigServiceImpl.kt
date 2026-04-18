package com.dcd.server.core.domain.domain.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.domain.exception.HttpConfigFailureException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.domain.service.GenerateHttpConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GenerateHttpConfigServiceImpl(
    private val commandPort: CommandPort,
    @Value("\${domain.config-path:.}")
    private val domainConfigPath: String
) : GenerateHttpConfigService {

    override fun generateWebServerConfig(application: Application, domain: Domain) {
        val webServerConfig = FileContent.getApplicationHttpConfig(application, domain.getDomainName())
        val httpConfigDirectory = "${domainConfigPath}/nginx/conf/${domain.id}"
        val configFileName = "${application.name.replace(" ", "-")}-http.conf"
        val commandResult = commandPort.executeShellCommand(
            "mkdir -p '$httpConfigDirectory' && " +
            "cat <<'EOF' > '$httpConfigDirectory/$configFileName'\n${webServerConfig}\nEOF"
        )

        if (commandResult.exitValue != 0)
            throw HttpConfigFailureException()
    }
}