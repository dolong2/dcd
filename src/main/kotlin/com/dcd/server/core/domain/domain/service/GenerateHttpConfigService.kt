package com.dcd.server.core.domain.domain.service

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.domain.model.Domain

interface GenerateHttpConfigService {
    fun generateWebServerConfig(application: Application, domain: Domain)
}