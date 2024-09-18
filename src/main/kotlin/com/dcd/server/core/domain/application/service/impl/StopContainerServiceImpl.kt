package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.StopContainerService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StopContainerServiceImpl(
    private val commandPort: CommandPort,
    private val eventPublisher: ApplicationEventPublisher
) : StopContainerService {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun stopContainer(application: Application) {
        val exitValue = commandPort.executeShellCommand("docker stop ${application.name.lowercase()}")
        if (exitValue != 0) {
            log.error("$exitValue")
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application))
        }

        eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.STOPPED, application))
    }
}