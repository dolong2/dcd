package com.dcd.server.core.domain.env.model

import com.dcd.server.core.domain.application.model.Application
import java.util.*

class ApplicationEnvMatcher(
    val id: UUID = UUID.randomUUID(),
    val application: Application,
    val applicationEnv: ApplicationEnv,
)