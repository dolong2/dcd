package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.annotation.ApplicationOwnerVerification
import com.dcd.server.core.domain.application.exception.AlreadyStoppedException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.service.StopContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class StopApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val stopContainerService: StopContainerService,
    private val changeApplicationStatusService: ChangeApplicationStatusService
) {
    @ApplicationOwnerVerification
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.STOPPED)
            throw AlreadyStoppedException()

        stopContainerService.stopContainer(application)

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.STOPPED)
    }
}