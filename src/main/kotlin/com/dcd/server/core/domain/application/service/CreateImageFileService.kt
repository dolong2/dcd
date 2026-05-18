package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CreateImageFileService {
    suspend fun createFileByApplicationId(id: String)
    suspend fun createFileToApplication(application: Application)
}
