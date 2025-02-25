package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import org.springframework.stereotype.Service

@Service
class GetApplicationVersionServiceImpl(
    private val commandPort: CommandPort
) : GetApplicationVersionService {
    override fun getAvailableVersion(applicationType: ApplicationType): List<String> {
        val (baseImageName, minVersion) = when (applicationType) {
            ApplicationType.SPRING_BOOT -> "openjdk" to "12"
            ApplicationType.NEST_JS -> "node" to "17"
            ApplicationType.MARIA_DB -> "mariadb" to "10"
            ApplicationType.MYSQL -> "mysql" to "8"
            ApplicationType.REDIS -> "redis" to "6"
        }
        val getVersionScript = FileContent.getImageVersionShellScriptContent(baseImageName, minVersion)
        return commandPort.executeShellCommandWithResult(getVersionScript)
    }
}