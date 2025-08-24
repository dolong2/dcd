package com.dcd.server.core.domain.workspace.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.workspace.service.CreateNetworkService
import org.springframework.stereotype.Service

@Service
class CreateNetworkServiceImpl(
    private val commandPort: CommandPort
) : CreateNetworkService {
    override fun createNetwork(networkTitle: String) {
        commandPort.executeShellCommand("docker network create --driver bridge $networkTitle")
    }
}