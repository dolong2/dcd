package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CreateContainerService {
    fun createContainer(id: String)
    fun createContainer(application: Application)
}