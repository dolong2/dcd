package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@UseCase
class RunApplicationUseCase(
    private val dockerRunService: DockerRunService,
    private val queryApplicationPort: QueryApplicationPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val changeApplicationStatusService: ChangeApplicationStatusService
) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        validateWorkspaceOwnerService.validateOwner(application.workspace)

        dockerRunService.runApplication(application)

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING)
    }
}