package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@ReadOnlyUseCase
class StopApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService
) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        deleteContainerService.deleteContainer(application)
        deleteApplicationDirectoryService.deleteApplicationDirectory(application)
    }
}