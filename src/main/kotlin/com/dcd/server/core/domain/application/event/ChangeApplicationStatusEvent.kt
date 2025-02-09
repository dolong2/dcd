package com.dcd.server.core.domain.application.event

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.util.FailureCase

class ChangeApplicationStatusEvent(
    val status: ApplicationStatus,
    val application: Application,
    val failureCase: FailureCase? = null
)