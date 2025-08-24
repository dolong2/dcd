package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface DeleteImageService {
    suspend fun deleteImage(application: Application)
}