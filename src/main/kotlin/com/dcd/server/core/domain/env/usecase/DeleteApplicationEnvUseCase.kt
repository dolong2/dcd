package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.event.DeployApplicationEvent
import com.dcd.server.core.domain.env.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun execute(envId: UUID) {
        val applicationEnv = queryApplicationEnvPort.findById(envId)
            ?: throw ApplicationEnvNotFoundException()
        commandApplicationEnvPort.delete(applicationEnv)

        val envMatcher = queryApplicationEnvPort.findAllMatcherByEnv(applicationEnv)
        val applicationSet = envMatcher.map { it.application }.toSet()
        if (applicationSet.isNotEmpty()) {
            eventPublisher.publishEvent(DeployApplicationEvent(applicationSet.map { it.id }))
        }
    }
}