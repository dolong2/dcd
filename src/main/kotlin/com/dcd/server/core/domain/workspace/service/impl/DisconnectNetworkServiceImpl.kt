package com.dcd.server.core.domain.workspace.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.DisconnectNetworkService
import org.springframework.stereotype.Service

@Service
class DisconnectNetworkServiceImpl(
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort
) : DisconnectNetworkService {
    override fun disconnectNetwork(workspace: Workspace) {
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace)
        applicationList.forEach {
            commandPort.executeShellCommand("docker network disconnect ${workspace.networkName} ${it.containerName}")
        }
    }
}