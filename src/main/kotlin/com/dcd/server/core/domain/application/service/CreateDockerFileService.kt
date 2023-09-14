package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CreateDockerFileService {
    fun createFileByApplicationId(id: String, javaVersion: Int)
    fun createFileToApplication(application: Application, javaVersion: Int)
}