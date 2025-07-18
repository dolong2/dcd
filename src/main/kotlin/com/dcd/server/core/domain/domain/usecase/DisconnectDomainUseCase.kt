package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.domain.exception.DomainNotFoundException
import com.dcd.server.core.domain.domain.service.RebootNginxService
import com.dcd.server.core.domain.domain.service.RemoveHttpConfigService
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort

@UseCase
class DisconnectDomainUseCase(
    private val queryDomainPort: QueryDomainPort,
    private val commandDomainPort: CommandDomainPort,
    private val removeHttpConfigService: RemoveHttpConfigService,
    private val rebootNginxService: RebootNginxService,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(domainId: String) {
        val domain = (queryDomainPort.findById(domainId)
            ?: throw DomainNotFoundException())

        if (workspaceInfo.workspace != domain.workspace)
            throw DomainNotFoundException()

        val updatedDomain = domain.copy(application = null)
        commandDomainPort.save(updatedDomain)

        removeHttpConfigService.removeHttpConfig(domain)
        rebootNginxService.rebootNginx()
    }
}