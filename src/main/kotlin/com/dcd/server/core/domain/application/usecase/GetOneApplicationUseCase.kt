package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toResDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationInitialScriptPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort

@UseCase(readOnly = true)
class GetOneApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val queryApplicationInitialScriptPort: QueryApplicationInitialScriptPort
) {
    fun execute(id: String): ApplicationResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        return application.toResDto()
    }
}