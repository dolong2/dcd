package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.response.ApplicationLogResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.GetContainerLogService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@ReadOnlyUseCase
class GetApplicationLogUseCase(
    private val getContainerLogService: GetContainerLogService,
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(id: String): ApplicationLogResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        val logs = getContainerLogService.getLogs(application)
        return ApplicationLogResDto(logs)
    }
}