package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@UseCase
class AddApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    fun execute(id: String, addApplicationEnvReqDto: AddApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        validateWorkspaceOwnerService.validateOwner(application.workspace)
        val envMutable = application.env.toMutableMap()
        addApplicationEnvReqDto.envList.forEach {
            envMutable[it.key] = it.value
        }
        commandApplicationPort.save(application.copy(env = envMutable))
    }
}