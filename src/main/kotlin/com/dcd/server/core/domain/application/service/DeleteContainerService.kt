package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface DeleteContainerService {
    suspend fun deleteContainer(application: Application)
}