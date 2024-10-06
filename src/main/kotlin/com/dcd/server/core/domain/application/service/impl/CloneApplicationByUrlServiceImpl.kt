package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.CloneApplicationByUrlService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class CloneApplicationByUrlServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val checkExitValuePort: CheckExitValuePort,
    private val commandPort: CommandPort
) : CloneApplicationByUrlService {
    override suspend fun cloneById(id: String) {
        withContext(Dispatchers.IO) {
            val application = (queryApplicationPort.findById(id)
                ?: throw ApplicationNotFoundException())
            val githubUrl = application.githubUrl
            val exitValue = commandPort.executeShellCommand("git clone $githubUrl ${application.name}")
            checkExitValuePort.checkApplicationExitValue(exitValue, application, this)
        }
    }

    override suspend fun cloneByApplication(application: Application) {
        withContext(Dispatchers.IO) {
            val githubUrl = application.githubUrl
            commandPort.executeShellCommand("git clone $githubUrl ${application.name}")
                .also {exitValue ->
                    checkExitValuePort.checkApplicationExitValue(exitValue, application, this)
                }
        }
    }
}