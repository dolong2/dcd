package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CreateContainerService {
    suspend fun createContainer(application: Application, externalPort: Int)
}