package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import java.util.UUID

@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val commandApplicationEnvPort: CommandApplicationEnvPort
) {
    fun execute(envId: UUID) {
        val applicationEnv = queryApplicationEnvPort.findById(envId)
            ?: throw ApplicationEnvNotFoundException()
        commandApplicationEnvPort.delete(applicationEnv)
    }
}