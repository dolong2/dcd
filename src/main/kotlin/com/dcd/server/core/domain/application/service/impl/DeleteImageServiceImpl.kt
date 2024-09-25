package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteImageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeleteImageServiceImpl(
    private val commandPort: CommandPort
) : DeleteImageService {
    override suspend fun deleteImage(application: Application) {
        withContext(Dispatchers.IO) {
            commandPort.executeShellCommand("docker rmi ${application.name.lowercase()}")
        }
    }
}