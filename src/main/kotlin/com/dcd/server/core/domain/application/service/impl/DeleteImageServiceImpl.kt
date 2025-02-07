package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteImageService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeleteImageServiceImpl(
    private val commandPort: CommandPort,
    private val checkExitValuePort: CheckExitValuePort
) : DeleteImageService {
    override suspend fun deleteImage(application: Application) {
        withContext(Dispatchers.IO) {
            commandPort.executeShellCommand("docker rmi ${application.containerName}")
                .also {exitValue ->
                    if (exitValue != 0 && exitValue != 1)
                        checkExitValuePort.checkApplicationExitValue(exitValue, application, this, "이미지 삭제중 에러")
                }
        }
    }
}