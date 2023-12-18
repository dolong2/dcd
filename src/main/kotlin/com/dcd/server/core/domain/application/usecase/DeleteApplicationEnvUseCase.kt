package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val getCurrentUserService: GetCurrentUserService,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    fun execute(id: String, key: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        validateWorkspaceOwnerService.validateOwner(getCurrentUserService.getCurrentUser(), application.workspace)
        val updatedEnv = application.env.toMutableMap()
        updatedEnv.remove(key)
            ?: throw ApplicationEnvNotFoundException()
        commandApplicationPort.save(application.copy(env = updatedEnv))
    }
}