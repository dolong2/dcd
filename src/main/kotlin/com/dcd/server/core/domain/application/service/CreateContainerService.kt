package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CreateContainerService {
    fun createContainer(application: Application, externalPort: Int)

    fun createContainer(application: Application, version: String, externalPort: Int)
}