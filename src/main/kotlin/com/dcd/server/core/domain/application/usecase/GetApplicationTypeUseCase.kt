package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.response.ApplicationTypeListResDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType

@ReadOnlyUseCase
class GetApplicationTypeUseCase {
    fun execute(): ApplicationTypeListResDto {
        val typeList = ApplicationType.values().map { it.name }

        return ApplicationTypeListResDto(typeList)
    }
}