package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import org.springframework.stereotype.Service

@Service
class ChangeApplicationStatusServiceImpl(
    private val commandApplicationPort: CommandApplicationPort
) : ChangeApplicationStatusService {
    override fun changeApplicationStatus(application: Application, status: ApplicationStatus) {
        commandApplicationPort.save(application.copy(status = status))
    }
}