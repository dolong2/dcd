package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface RunContainerService {
    suspend fun runContainer(id: String)

   suspend fun runContainer(application: Application)
}