package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface GenerateWebServerConfigService {
    fun generateWebServerConfig(application: Application, domain: String)
}