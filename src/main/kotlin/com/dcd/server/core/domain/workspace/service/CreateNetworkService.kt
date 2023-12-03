package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.common.command.CommandPort
import org.springframework.stereotype.Service

@Service
class CreateNetworkService(
    private val commandPort: CommandPort
) {
    fun createNetwork(networkTitle: String) {
        commandPort.executeShellCommand("docker network create --driver bridge ${networkTitle.replace(' ', '_')}")
    }
}