package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface GenerateHttpConfigService {
    fun generateWebServerConfig(application: Application, domain: String)
}