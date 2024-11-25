package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.request.SetDomainReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.InvalidDomainFormatException
import com.dcd.server.core.domain.application.service.GenerateHttpConfigService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException

@UseCase
class SetApplicationDomainUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val getCurrentUserService: GetCurrentUserService,
    private val generateHttpConfigService: GenerateHttpConfigService
) {
    fun execute(applicationId: String, setDomainReqDto: SetDomainReqDto) {
        val domain = setDomainReqDto.domain
        require(domain.matches(Regex("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$"))) {
            throw InvalidDomainFormatException()
        }

        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        val currentUser = getCurrentUserService.getCurrentUser()
        if (currentUser.id != application.workspace.owner.id)
            throw WorkspaceOwnerNotSameException()

        generateHttpConfigService.generateWebServerConfig(application, domain)
    }
}