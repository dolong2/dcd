package com.dcd.server.core.domain.auth.service

interface VerifyEmailAuthService {
    fun verifyCode(email: String, code: String)
}