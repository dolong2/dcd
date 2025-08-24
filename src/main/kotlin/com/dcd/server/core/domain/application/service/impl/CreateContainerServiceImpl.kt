package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.CreateContainerService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.util.FailureCase
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
                "docker create --network ${application.workspace.networkName} " +
                        "--name ${application.containerName} " +
                        "-p ${externalPort}:${application.port} ${application.containerName}:latest"

            commandPort.executeShellCommand(cmd)
                .also {exitValue ->
                    checkExitValuePort.checkApplicationExitValue(exitValue, application, this, FailureCase.CREATE_CONTAINER_FAILURE)
                    if (exitValue != 0) {
                        commandPort.executeShellCommand("rm -rf ${application.name}")
                        return@withContext
                    }
                }

            val dcdNetworkConnectCmd = "docker network connect dcd ${application.containerName}"
            commandPort.executeShellCommand(dcdNetworkConnectCmd)
                .also {exitValue ->
                    checkExitValuePort.checkApplicationExitValue(exitValue, application, this, FailureCase.CONNECT_NETWORK_FAILURE)
                    if (exitValue != 0) {
                        commandPort.executeShellCommand("rm -rf ${application.name}")
                        return@withContext
                    }
                }
        }
    }
}