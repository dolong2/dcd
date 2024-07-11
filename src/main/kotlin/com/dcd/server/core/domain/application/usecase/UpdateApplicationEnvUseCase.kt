package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class UpdateApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort
) {
    fun execute(applicationId: String, envKey: String, updateApplicationEnvReqDto: UpdateApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        val env = application.env
        if (env.containsKey(envKey).not())
            throw ApplicationEnvNotFoundException()

        val mutableEnv = env.toMutableMap()
        mutableEnv[envKey] = updateApplicationEnvReqDto.newValue
        commandApplicationPort.save(
            application.copy(
                env = mutableEnv
            )
        )
    }
}