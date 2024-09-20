package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import org.springframework.stereotype.Service

@Service
class GetApplicationVersionServiceImpl(
    private val commandPort: CommandPort
) : GetApplicationVersionService {
    override fun getAvailableVersion(applicationType: ApplicationType): List<String> {
        val commandResult = commandPort.executeShellCommandWithResult("docker images ${applicationType.name.lowercase()}")
        val result = mutableListOf<String>()
        var first = true
        commandResult.forEach {
            if (first) first = !first
            else {
                val split = it.replace(Regex("\\s{2,}"), " ").split(" ")
                val tag = split[1]
                result.add(tag)
            }
        }
        return result
    }
}