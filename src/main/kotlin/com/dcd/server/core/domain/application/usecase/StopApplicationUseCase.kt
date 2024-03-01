package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.exception.AlreadyStoppedException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.service.StopContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@ReadOnlyUseCase
class StopApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val stopContainerService: StopContainerService,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val changeApplicationStatusService: ChangeApplicationStatusService
) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.STOPPED)
            throw AlreadyStoppedException()

        validateWorkspaceOwnerService.validateOwner(application.workspace)

        stopContainerService.stopContainer(application)

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.STOPPED)
    }
}