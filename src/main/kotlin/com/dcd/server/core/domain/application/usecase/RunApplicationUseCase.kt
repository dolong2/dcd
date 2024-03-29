package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.aop.annotation.ApplicationOwnerVerification
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class RunApplicationUseCase(
    private val runContainerService: RunContainerService,
    private val queryApplicationPort: QueryApplicationPort,
    private val changeApplicationStatusService: ChangeApplicationStatusService
) {
    @ApplicationOwnerVerification
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        runContainerService.runApplication(application)

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING)
    }
}