package com.dcd.server.core.domain.workspace.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.workspace.service.DeleteNetworkService
import org.springframework.stereotype.Service

@Service
class DeleteNetworkServiceImpl(
    private val commandPort: CommandPort
) : DeleteNetworkService {
    override fun deleteNetwork(networkTitle: String) {
        commandPort.executeShellCommand("docker network rm ${networkTitle.replace(' ', '_')}")
    }
}