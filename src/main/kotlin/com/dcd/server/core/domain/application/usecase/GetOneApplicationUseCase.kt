package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@ReadOnlyUseCase
class GetOneApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort
) {
    @WorkspaceOwnerVerification
    fun execute(id: String): ApplicationResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        return application.toDto()
    }
}