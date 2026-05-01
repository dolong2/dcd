package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.response.ApplicationLogResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase(readOnly = true)
class GetApplicationLogUseCase(
    private val containerPort: ContainerPort,
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(id: String): ApplicationLogResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        val logs = containerPort.execute { getContainerLogs(application) } ?: emptyList()
        return ApplicationLogResDto(logs)
    }
}