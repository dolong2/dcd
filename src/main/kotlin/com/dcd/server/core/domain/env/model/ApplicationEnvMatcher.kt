package com.dcd.server.core.domain.env.model

import com.dcd.server.core.domain.application.model.Application
import java.util.*

class ApplicationEnvMatcher(
    val id: UUID = UUID.randomUUID(),
    val application: Application,
    val applicationEnv: ApplicationEnv,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ApplicationEnvMatcher) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}