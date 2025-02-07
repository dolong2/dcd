package com.dcd.server.core.domain.application.spi.adapter

import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class CheckExitValueAdapter(
    private val eventPublisher: ApplicationEventPublisher
) : CheckExitValuePort {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun checkApplicationExitValue(exitValue: Int, application: Application, failureReason: String?) {
        if (exitValue != 0) {
            log.error("${application.name} - $exitValue")
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, failureReason))
        }
    }

    override fun checkApplicationExitValue(exitValue: Int, application: Application, coroutineScope: CoroutineScope, failureReason: String?) {
        if (exitValue != 0) {
            log.error("${application.name} - $exitValue")
            eventPublisher.publishEvent(ChangeApplicationStatusEvent(ApplicationStatus.FAILURE, application, failureReason))
            coroutineScope.cancel()
        }
    }
}