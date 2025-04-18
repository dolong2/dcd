package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType

@ReadOnlyUseCase
class GetApplicationTypeUseCase {
    fun execute(): ListResDto<String> {
        val typeList = ApplicationType.values().map { it.name }

        return ListResDto(typeList)
    }
}