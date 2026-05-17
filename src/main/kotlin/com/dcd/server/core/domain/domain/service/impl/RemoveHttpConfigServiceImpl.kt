package com.dcd.server.core.domain.domain.service.impl

import com.dcd.server.core.common.file.exception.FileOperationException
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.domain.domain.exception.DomainNotConnectedException
import com.dcd.server.core.domain.domain.exception.HttpConfigRemoveFailureException
import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.domain.service.RemoveHttpConfigService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class RemoveHttpConfigServiceImpl(
    private val fileOperationPort: FileOperationPort,
    @Value("\${domain.config-path:.}")
    private val domainConfigPath: String
) : RemoveHttpConfigService {
    override fun removeHttpConfig(domain: Domain) {
        if (domain.application == null)
            throw DomainNotConnectedException()

        val httpConfigDirectory = Paths.get(domainConfigPath, "nginx", "conf", domain.id)

        try {
            fileOperationPort.deleteDirectory(httpConfigDirectory)
        } catch (e: FileOperationException) {
            throw HttpConfigRemoveFailureException()
        }
    }
}