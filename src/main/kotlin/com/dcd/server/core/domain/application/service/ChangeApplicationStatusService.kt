package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus

interface ChangeApplicationStatusService {
    fun changeApplicationStatus(application: Application, status: ApplicationStatus)
}