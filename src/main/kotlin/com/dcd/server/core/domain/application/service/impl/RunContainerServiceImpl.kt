package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.ContainerNotRunException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.RunContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RunContainerServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : RunContainerService {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override suspend fun runApplication(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        run(application)
    }

    override suspend fun runApplication(application: Application) {
        run(application)
    }

    private suspend fun run(application: Application) {
        withContext(Dispatchers.IO) {
            val exitValue = commandPort.executeShellCommand("docker start ${application.name.lowercase()}")
            if (exitValue != 0) log.error("$exitValue")
        }
    }
}