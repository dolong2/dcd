package com.dcd.server.core.domain.workspace.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.workspace.service.DisconnectNetworkService
import org.springframework.stereotype.Service

@Service
class DisconnectNetworkServiceImpl(
    private val commandPort: CommandPort
) : DisconnectNetworkService {
    override fun disconnectNetwork(networkName: String) {
        commandPort.executeShellCommand(
            "for container in \$(docker network inspect $networkName -f '{{range .Containers}}{{.Name}} {{end}}'); do" +
                " docker network disconnect $networkName \$container;" +
                " done"
        )
    }
}