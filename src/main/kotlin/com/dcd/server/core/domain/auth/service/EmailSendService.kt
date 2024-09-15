package com.dcd.server.core.domain.auth.service

interface EmailSendService {
    suspend fun sendEmail(email: String)
}