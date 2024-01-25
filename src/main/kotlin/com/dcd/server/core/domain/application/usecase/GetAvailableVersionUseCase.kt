package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.response.AvailableVersionResDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.GetApplicationVersionService

@UseCase
class GetAvailableVersionUseCase(
    private val getApplicationVersionService: GetApplicationVersionService
) {
    fun execute(applicationType: ApplicationType): AvailableVersionResDto {
        val availableVersion = getApplicationVersionService.getAvailableVersion(applicationType)
        return AvailableVersionResDto(availableVersion)
    }
}