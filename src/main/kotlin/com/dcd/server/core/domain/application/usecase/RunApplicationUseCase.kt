package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@UseCase
class RunApplicationUseCase(
    private val runContainerService: RunContainerService,
    private val queryApplicationPort: QueryApplicationPort,
    private val changeApplicationStatusService: ChangeApplicationStatusService
): CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        launch  {
            runContainerService.runApplication(application)
        }

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.PENDING)
    }
}