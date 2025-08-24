package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface BuildDockerImageService {
    suspend fun buildImageByApplicationId(id: String)
    suspend fun buildImageByApplication(application: Application)
}