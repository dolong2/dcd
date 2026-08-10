package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.EmailSendReqDto
import com.dcd.server.core.domain.auth.service.EmailSendService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

@UseCase
class AuthMailSendUseCase(
    private val emailSendService: EmailSendService
) : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()){
    private val log = LoggerFactory.getLogger(this::class.java)

    fun execute(emailSendReqDto: EmailSendReqDto) {
        launch {
            try {
                emailSendService.sendEmail(emailSendReqDto.email, emailSendReqDto.usage)
            } catch (e: Exception) {
                log.error("Failed to send email: ${emailSendReqDto.email}", e)
            }
        }
    }
}