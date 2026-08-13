package com.dcd.server.core.domain.application.model

import java.util.UUID

class ApplicationInitialScript(
    val id: UUID,
    val script: String,
    val application: Application
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ApplicationInitialScript) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}