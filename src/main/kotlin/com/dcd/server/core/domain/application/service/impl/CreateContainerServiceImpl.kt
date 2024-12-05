package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.CreateContainerService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class CreateContainerServiceImpl(
    private val commandPort: CommandPort,
    private val checkExitValuePort: CheckExitValuePort
) : CreateContainerService {
    override suspend fun createContainer(application: Application, externalPort: Int) {
        withContext(Dispatchers.IO) {
            val cmd =
                "docker create --network ${application.workspace.title.replace(' ', '_')} " +
                        "--name ${application.name.lowercase()} " +
                        "-p ${externalPort}:${application.port} ${application.name.lowercase()}:latest"

            commandPort.executeShellCommand(cmd)
                .also {exitValue ->
                    checkExitValuePort.checkApplicationExitValue(exitValue, application, this)
                }

            val dcdNetworkConnectCmd = "docker network connect dcd ${application.name.lowercase()}"
            commandPort.executeShellCommand(dcdNetworkConnectCmd)
                .also {exitValue ->
                    checkExitValuePort.checkApplicationExitValue(exitValue, application, this)
                }
        }
    }
}