package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.EmailSendReqDto
import com.dcd.server.core.domain.auth.service.EmailSendService


@UseCase
class AuthMailSendUseCase(
    private val emailSendService: EmailSendService
) {
    fun execute(emailSendReqDto: EmailSendReqDto) {
        emailSendService.sendEmail(emailSendReqDto.email)
    }
}