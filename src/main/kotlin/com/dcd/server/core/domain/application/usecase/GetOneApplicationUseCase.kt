package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException

@ReadOnlyUseCase
class GetOneApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val getCurrentUserService: GetCurrentUserService
) {
    fun execute(id: String): ApplicationResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val currentUser = getCurrentUserService.getCurrentUser()
        if (application.workspace.owner.equals(currentUser).not())
            throw WorkspaceOwnerNotSameException()
        return application.toDto()
    }
}