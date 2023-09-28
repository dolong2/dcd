package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResponseDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class GetOneApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort
) {
    fun execute(id: String): ApplicationResponseDto =
        (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
            .toDto()
}