package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface ModifyGradleService {
    suspend fun modifyGradleByApplicationId(id: String)
    suspend fun modifyGradleByApplication(application: Application)
}