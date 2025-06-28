package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType

@UseCase(readOnly = true)
class GetApplicationTypeUseCase {
    fun execute(): ListResDto<String> {
        val typeList = ApplicationType.values().map { it.name }

        return ListResDto(typeList)
    }
}