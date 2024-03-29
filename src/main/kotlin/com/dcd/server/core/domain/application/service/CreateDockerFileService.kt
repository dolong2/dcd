package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CreateDockerFileService {
    fun createFileByApplicationId(id: String, version: String)
    fun createFileToApplication(application: Application, version: String)
}