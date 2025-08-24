package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.util.FailureCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DeleteApplicationDirectoryServiceImpl(
    private val commandPort: CommandPort,
    private val checkExitValuePort: CheckExitValuePort
) : DeleteApplicationDirectoryService {
    override suspend fun deleteApplicationDirectory(application: Application) {
        withContext(Dispatchers.IO) {
            commandPort.executeShellCommand("rm -rf '${application.name}'")
                .also {exitValue ->
                    checkExitValuePort.checkApplicationExitValue(exitValue, application, this, FailureCase.DELETE_DIRECTORY_FAILURE)
                }
        }
    }
}