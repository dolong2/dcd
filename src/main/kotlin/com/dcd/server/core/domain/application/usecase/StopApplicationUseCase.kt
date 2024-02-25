package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@ReadOnlyUseCase
class StopApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val changeApplicationStatusService: ChangeApplicationStatusService
) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        validateWorkspaceOwnerService.validateOwner(application.workspace)
        deleteContainerService.deleteContainer(application)
        deleteApplicationDirectoryService.deleteApplicationDirectory(application)
        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.STOPPED)
    }
}