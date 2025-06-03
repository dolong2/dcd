package com.dcd.server.core.domain.auth.spi

interface ParseTokenPort {
    fun parseToken(token: String): String?

    fun getJwtType(token: String): String

    fun getUserId(token: String): String
}
