package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.GetContainerLogService
import org.springframework.stereotype.Service

@Service
class GetContainerLogServiceImpl(
    private val commandPort: CommandPort
) : GetContainerLogService {
    override fun getLogs(application: Application): List<String> =
        commandPort.executeShellCommandWithResult("docker logs ${application.name.lowercase()}")
}