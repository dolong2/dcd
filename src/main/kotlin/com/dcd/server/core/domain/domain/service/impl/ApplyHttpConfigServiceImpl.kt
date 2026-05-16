package com.dcd.server.core.domain.domain.service.impl

import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.domain.service.ApplyHttpConfigService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ApplyHttpConfigServiceImpl(
    private val containerPort: ContainerPort
) : ApplyHttpConfigService {
    private val log = LoggerFactory.getLogger(this::class.simpleName)

    override fun applyHttpConfig() {
        try {
            containerPort.execute {
                executeCmd("dcd-nginx", "nginx -s reload")
            }
        } catch (e: Exception) {
            log.error("Failed to apply HTTP config", e)
            throw e
        }
    }
}