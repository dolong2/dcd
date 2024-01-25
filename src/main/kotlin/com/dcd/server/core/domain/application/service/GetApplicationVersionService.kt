package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface GetApplicationVersionService {
    fun getAvailableVersion(application: Application): List<String>
}