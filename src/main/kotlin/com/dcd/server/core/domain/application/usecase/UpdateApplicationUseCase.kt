package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException

@UseCase
class UpdateApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val getCurrentUserService: GetCurrentUserService
) {
    fun execute(id: String, updateApplicationReqDto: UpdateApplicationReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val owner = application.workspace.owner

        if (owner.equals(getCurrentUserService.getCurrentUser()).not())
            throw WorkspaceOwnerNotSameException()

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        val updatedApplication =
            application.copy(
                name = updateApplicationReqDto.name,
                description = updateApplicationReqDto.description,
                applicationType = updateApplicationReqDto.applicationType,
                githubUrl = updateApplicationReqDto.githubUrl,
                version = updateApplicationReqDto.version,
                port = updateApplicationReqDto.port
            )
        commandApplicationPort.save(updatedApplication)
    }
}