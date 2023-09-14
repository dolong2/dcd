package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface ModifyGradleService {
    fun modifyGradleByApplicationId(id: String)
    fun modifyGradleByApplication(application: Application)
}