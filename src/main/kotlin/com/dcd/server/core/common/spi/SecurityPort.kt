package com.dcd.server.core.common.spi

interface SecurityPort {
    fun getCurrentUserId(): String
    fun encodeRawPassword(rawPassword: String): String
    fun isCorrectPassword(rawPassword: String, encodedPassword: String): Boolean
}