package com.dcd.server.core.domain.application.event

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus

class ChangeApplicationStatusEvent(
    val status: ApplicationStatus,
    val application: Application,
    val failureReason: String? = null
)