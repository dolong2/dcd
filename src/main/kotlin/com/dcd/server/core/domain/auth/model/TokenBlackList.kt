package com.dcd.server.core.domain.auth.model

class TokenBlackList(
    val token: String,
    val ttl: Long,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is TokenBlackList) return false
        return this.token == other.token
    }

    override fun hashCode(): Int {
        return this.token.hashCode()
    }
}