package com.dcd.server.core.domain.auth.service

interface EmailSendService {
    fun sendEmail(email: String)
}