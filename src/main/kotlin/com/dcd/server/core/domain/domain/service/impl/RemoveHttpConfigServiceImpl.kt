package com.dcd.server.core.domain.domain.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.domain.exception.DomainNotConnectedException
import com.dcd.server.core.domain.domain.exception.HttpConfigRemoveFailureException
import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.domain.service.RemoveHttpConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RemoveHttpConfigServiceImpl(
    private val commandPort: CommandPort,
    @Value("\${domain.config-path:.}")
    private val domainConfigPath: String
) : RemoveHttpConfigService {
    override fun removeHttpConfig(domain: Domain) {
        if (domain.application == null)
            throw DomainNotConnectedException()

        val httpConfigDirectory = "${domainConfigPath}/nginx/conf/${domain.id}"
        val exitValue = commandPort.executeShellCommand("rm -r $httpConfigDirectory")

        if (exitValue != 0)
            throw HttpConfigRemoveFailureException()
    }
}