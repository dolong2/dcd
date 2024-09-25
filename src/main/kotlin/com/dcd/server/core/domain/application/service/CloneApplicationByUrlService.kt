package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CloneApplicationByUrlService {
    suspend fun cloneById(id: String)
    suspend fun cloneByApplication(application: Application)
}