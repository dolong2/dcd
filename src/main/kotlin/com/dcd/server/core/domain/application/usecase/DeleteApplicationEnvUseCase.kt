package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort
) {
    @WorkspaceOwnerVerification
    fun execute(id: String, key: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val updatedEnv = application.env.toMutableMap()
        updatedEnv.remove(key)
            ?: throw ApplicationEnvNotFoundException()
        commandApplicationPort.save(application.copy(env = updatedEnv))
    }
}