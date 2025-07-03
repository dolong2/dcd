package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.exception.HttpConfigFailureException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.GenerateHttpConfigService
import org.springframework.stereotype.Service

@Service
class GenerateHttpConfigServiceImpl(
    private val commandPort: CommandPort,
) : GenerateHttpConfigService {
    override fun generateWebServerConfig(application: Application, domain: String) {
        val webServerConfig = FileContent.getApplicationHttpConfig(application, domain)
        //TODO 환경변수 + 절대경로로 수정 필요
        val httpConfigDirectory = "./nginx/conf/${application.workspace.id}"
        val exitValue = commandPort.executeShellCommand(
            "mkdir -p $httpConfigDirectory && " +
            "cat <<EOF > ${httpConfigDirectory}/${application.name.replace(" ", "-")}-http.conf \n ${webServerConfig}EOF"
        )

        if (exitValue != 0)
            throw HttpConfigFailureException()
    }
}