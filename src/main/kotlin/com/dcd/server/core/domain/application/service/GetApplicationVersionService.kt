package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.enums.ApplicationType

interface GetApplicationVersionService {
    fun getAvailableVersion(applicationType: ApplicationType): List<String>
}