package com.dcd.server.core.domain.application.event.listener

import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ApplicationEventListener(
    private val commandApplicationPort: CommandApplicationPort
) {
    @EventListener
    @Transactional(rollbackFor = [Exception::class])
    fun process(event: ChangeApplicationStatusEvent) {
        val updatedApplication = event.application.copy(
            status = event.status,
            failureReason = event.failureReason
        )

        commandApplicationPort.save(updatedApplication)
    }
}