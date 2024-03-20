package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.annotation.ApplicationOwnerVerification
import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsEnvException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class AddApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort
) {
    @ApplicationOwnerVerification
    fun execute(id: String, addApplicationEnvReqDto: AddApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val envMutable = application.env.toMutableMap()
        addApplicationEnvReqDto.envList.forEach {
            if (envMutable.containsKey(it.key)) throw AlreadyExistsEnvException()

            envMutable[it.key] = it.value
        }
        commandApplicationPort.save(application.copy(env = envMutable))
    }
}