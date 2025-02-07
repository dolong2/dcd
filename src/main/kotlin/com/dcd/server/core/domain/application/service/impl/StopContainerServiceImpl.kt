package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.StopContainerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class StopContainerServiceImpl(
    private val commandPort: CommandPort,
    private val eventPublisher: ApplicationEventPublisher
) : StopContainerService {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override suspend fun stopContainer(application: Application) {
        withContext(Dispatchers.IO) {
            val exitValue = commandPort.executeShellCommand("docker stop ${application.containerName}")
            if (exitValue != 0) {
                log.error("$exitValue")
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, "컨테이너 정지중 에러"))
            }

            else
                eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.STOPPED, application))
        }
    }
}