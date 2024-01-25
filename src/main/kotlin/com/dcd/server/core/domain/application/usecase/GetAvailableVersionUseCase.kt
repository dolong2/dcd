package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.response.AvailableVersionResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.GetApplicationVersionService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class GetAvailableVersionUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val getApplicationVersionService: GetApplicationVersionService
) {
    fun execute(id: String): AvailableVersionResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val availableVersion = getApplicationVersionService.getAvailableVersion(application)
        return AvailableVersionResDto(availableVersion)
    }
}