package com.dcd.server.core.domain.env.model

import java.util.UUID

data class ApplicationEnvDetail(
    val id: UUID,
    val key: String,
    val value: String,
    val encryption: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ApplicationEnvDetail) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}