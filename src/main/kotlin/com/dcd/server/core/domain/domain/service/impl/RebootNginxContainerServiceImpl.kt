package com.dcd.server.core.domain.domain.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.domain.service.RebootNginxService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RebootNginxContainerServiceImpl(
    private val commandPort: CommandPort
) : RebootNginxService {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun rebootNginx() {
        val exitValue = commandPort.executeShellCommand("docker restart dcd-nginx")
        if (exitValue != 0)
            log.error("nginx restart failure")
    }
}