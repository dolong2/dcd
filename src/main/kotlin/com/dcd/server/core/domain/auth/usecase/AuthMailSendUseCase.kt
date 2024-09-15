package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.EmailSendReqDto
import com.dcd.server.core.domain.auth.service.EmailSendService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@UseCase
class AuthMailSendUseCase(
    private val emailSendService: EmailSendService
) : CoroutineScope by CoroutineScope(Dispatchers.IO){
    fun execute(emailSendReqDto: EmailSendReqDto) {
        launch {
            emailSendService.sendEmail(emailSendReqDto.email)
        }
    }
}