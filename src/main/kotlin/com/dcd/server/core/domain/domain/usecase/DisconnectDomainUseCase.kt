package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.exception.DomainNotConnectedException
import com.dcd.server.core.domain.domain.service.*
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort

@UseCase
class DisconnectDomainUseCase(
    private val queryDomainPort: QueryDomainPort,
    private val commandDomainPort: CommandDomainPort,
    private val removeHttpConfigService: RemoveHttpConfigService,
    private val applyHttpConfigService: ApplyHttpConfigService,
    private val generateHttpConfigService: GenerateHttpConfigService,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(domainId: String) {
        val domain = (queryDomainPort.findById(domainId)
            ?: throw DomainNotFoundException())

        if (workspaceInfo.workspace != domain.workspace)
            throw DomainNotFoundException()

        if (domain.application == null)
            throw DomainNotConnectedException()

        val updatedDomain = domain.copy(application = null)
        commandDomainPort.save(updatedDomain)

        removeHttpConfigService.removeHttpConfig(domain)
        try {
            applyHttpConfigService.applyHttpConfig()
        } catch (e: Exception) {
            // HTTP config 적용 실패 시 도메인 연결 해제후 설정 롤백
            generateHttpConfigService.generateWebServerConfig(domain.application, domain)
            commandDomainPort.save(domain)
            throw e
        }
    }
}