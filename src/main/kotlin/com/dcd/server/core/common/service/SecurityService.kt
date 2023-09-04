package com.dcd.server.core.common.service

interface SecurityService {
    fun getCurrentUserId(): String
    fun encodePassword(rawPassword: String): String
    fun matchPassword(rawPassword: String, encodedPassword: String)
}