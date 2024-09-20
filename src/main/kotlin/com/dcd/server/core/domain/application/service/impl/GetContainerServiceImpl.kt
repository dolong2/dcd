package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.scheduler.enums.ContainerStatus
import com.dcd.server.core.domain.application.service.GetContainerService
import org.springframework.stereotype.Service

@Service
class GetContainerServiceImpl(
    private val commandPort: CommandPort
) : GetContainerService {
    override fun getContainerNameByStatus(status: ContainerStatus): List<String> =
        commandPort.executeShellCommandWithResult("docker ps -a --filter \"status=${status.description}\" --format \"{{.Names}}\"")
}