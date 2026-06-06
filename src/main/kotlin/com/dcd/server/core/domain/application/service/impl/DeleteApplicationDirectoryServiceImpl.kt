package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.util.FailureCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class DeleteApplicationDirectoryServiceImpl(
    private val fileOperationPort: FileOperationPort,
    private val eventPublisher: ApplicationEventPublisher
) : DeleteApplicationDirectoryService {
    override suspend fun deleteApplicationDirectory(application: Application) {
        withContext(Dispatchers.IO) {
            try {
                fileOperationPort.deleteDirectory(Paths.get(application.directoryName))
            } catch (e: FileOperationException) {
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, FailureCase.DELETE_DIRECTORY_FAILURE))
                this.cancel()
            }
        }
    }
}
