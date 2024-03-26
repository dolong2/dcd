package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface RunContainerService {
    fun runApplication(id: String)

    fun runApplication(application: Application)
}