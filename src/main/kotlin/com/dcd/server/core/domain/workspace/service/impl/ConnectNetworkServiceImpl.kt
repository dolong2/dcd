package com.dcd.server.core.domain.workspace.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ConnectNetworkService
import org.springframework.stereotype.Service

@Service
class ConnectNetworkServiceImpl(
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort
) : ConnectNetworkService{
    override fun connectNetworkByWorkspace(workspace: Workspace) {
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace)
        applicationList.forEach {
            commandPort.executeShellCommand("docker network connect ${workspace.networkName} ${it.containerName}")
        }
    }
}