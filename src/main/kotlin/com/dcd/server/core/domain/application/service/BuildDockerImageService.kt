package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface BuildDockerImageService {
    fun buildImageByApplicationId(id: String)
    fun buildImageByApplication(application: Application)
}