package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.CloneApplicationByUrlService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service

@Service
class CloneApplicationByUrlServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandPort: CommandPort
) : CloneApplicationByUrlService {
    override fun cloneById(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val githubUrl = application.githubUrl
        commandPort.executeShellCommand("git clone $githubUrl")
    }

    override fun cloneByApplication(application: Application) {
        val githubUrl = application.githubUrl
        commandPort.executeShellCommand("git clone $githubUrl")
    }
}