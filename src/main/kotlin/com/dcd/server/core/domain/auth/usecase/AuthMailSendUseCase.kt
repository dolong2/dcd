package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.EmailSendRequestData
import com.dcd.server.core.domain.auth.service.EmailSendService


@UseCase
class AuthMailSendUseCase(
    private val emailSendService: EmailSendService
) {
    fun execute(emailSendRequestData: EmailSendRequestData) {
        emailSendService.sendEmail(emailSendRequestData.email)
    }
}