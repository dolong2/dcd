package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteContainerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeleteContainerServiceImpl(
    private val commandPort: CommandPort
) : DeleteContainerService {
    override suspend fun deleteContainer(application: Application) {
        withContext(Dispatchers.IO) {
            val name = application.name.lowercase()
            commandPort.executeShellCommand("docker stop $name && docker rm $name")
        }
    }

}