package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface DeleteApplicationDirectoryService {
    suspend fun deleteApplicationDirectory(application: Application)
}