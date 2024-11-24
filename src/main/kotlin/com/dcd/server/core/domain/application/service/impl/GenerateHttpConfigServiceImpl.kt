package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.GenerateHttpConfigService
import org.springframework.stereotype.Service

@Service
class GenerateHttpConfigServiceImpl(
    private val commandPort: CommandPort,
) : GenerateHttpConfigService {
    override fun generateWebServerConfig(application: Application, domain: String) {
        val webServerConfig = FileContent.getApplicationHttpConfig(application, domain)
        commandPort.executeShellCommand("touch ./nginx/conf/${application.name.replace(" ", "-")}-http.conf >> EOF ${webServerConfig} >> EOF")
    }
}