package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import org.springframework.stereotype.Service

@Service
class DeleteApplicationDirectoryServiceImpl(
    private val commandPort: CommandPort
) : DeleteApplicationDirectoryService {
    override fun deleteApplicationDirectory(application: Application) {
        commandPort.executeShellCommand("rm -rf ${application.name}")
    }
}