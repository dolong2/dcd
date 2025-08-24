package com.dcd.server.core.domain.auth.service

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

interface EmailSendService {
    suspend fun sendEmail(email: String, usage: EmailAuthUsage)
}