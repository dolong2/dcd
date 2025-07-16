package com.dcd.server.core.domain.domain.service

import com.dcd.server.core.domain.domain.model.Domain

interface RemoveHttpConfigService {
    fun removeHttpConfig(domain: Domain)
}