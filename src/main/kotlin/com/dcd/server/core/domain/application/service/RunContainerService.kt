package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface RunContainerService {
    suspend fun runApplication(id: String)

   suspend fun runApplication(application: Application)
}