package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeleteApplicationDirectoryServiceImpl(
    private val commandPort: CommandPort
) : DeleteApplicationDirectoryService {
    override suspend fun deleteApplicationDirectory(application: Application) {
        withContext(Dispatchers.IO) {
            commandPort.executeShellCommand("rm -rf ${application.name}")
        }
    }
}