package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.domain.dto.request.ConnectDomainReqDto
import com.dcd.server.core.domain.domain.exception.AlreadyConnectedDomainException
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.service.GenerateHttpConfigService
import com.dcd.server.core.domain.domain.service.RebootNginxService
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort

@UseCase
class ConnectDomainUseCase(
    private val queryDomainPort: QueryDomainPort,
    private val commandDomainPort: CommandDomainPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val generateHttpConfigService: GenerateHttpConfigService,
    private val rebootNginxService: RebootNginxService
) {
    fun execute(domainId: String, connectDomainReqDto: ConnectDomainReqDto) {
        val domain = (queryDomainPort.findById(domainId)
            ?: throw DomainNotFoundException())

        if (domain.application != null)
            throw AlreadyConnectedDomainException()

        val application = (queryApplicationPort.findById(connectDomainReqDto.applicationId)
            ?: throw ApplicationNotFoundException())

        if (application.workspace != workspaceInfo.workspace)
            throw ApplicationNotFoundException() // 현재 사용중인 워크스페이스에 속해있지 않음

        val updatedDomain = domain.copy(application = application)
        commandDomainPort.save(updatedDomain)

        generateHttpConfigService.generateWebServerConfig(application, domain)
        rebootNginxService.rebootNginx()
    }
}