package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.CreateContainerService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class CreateContainerServiceImpl(
    private val commandPort: CommandPort,
    private val eventPublisher: ApplicationEventPublisher
) : CreateContainerService {
    private val log = LoggerFactory.getLogger(this::class.simpleName)
    override fun createContainer(application: Application, externalPort: Int) {
        val cmd =
            "docker create --network ${application.workspace.title.replace(' ', '_')} " +
            "--name ${application.name.lowercase()} " +
            "-p ${externalPort}:${application.port} ${application.name.lowercase()}:latest"

        val exitValue = commandPort.executeShellCommand(cmd)
        if (exitValue != 0) {
            log.error("$exitValue")
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application))
        }
    }
}