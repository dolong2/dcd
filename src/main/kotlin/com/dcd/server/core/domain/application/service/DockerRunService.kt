package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface DockerRunService {
    fun runApplication(id: String, externalPort: Int)

    fun runApplication(application: Application, externalPort: Int)

    fun runApplication(id: String, version: String, externalPort: Int)

    fun runApplication(application: Application, version: String, externalPort: Int)

}