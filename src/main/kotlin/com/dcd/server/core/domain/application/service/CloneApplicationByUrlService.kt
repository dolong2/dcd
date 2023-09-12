package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface CloneApplicationByUrlService {
    fun cloneById(id: String)
    fun cloneByApplication(application: Application)
}