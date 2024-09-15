package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.request.EmailSendReqDto
import com.dcd.server.core.domain.auth.service.EmailSendService
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class AuthMailSendUseCaseTest : BehaviorSpec({
    val emailSendService = mockk<EmailSendService>()
    val useCase = AuthMailSendUseCase(emailSendService)

    given("EmailSendRequestData가 주어지고") {
        val testEmail = "testEmail"
        val request = EmailSendReqDto(testEmail)
        `when`("execute메서드를 실행할때") {
            coEvery { emailSendService.sendEmail(testEmail) } returns Unit
            useCase.execute(request)
            then("emailSendService의 sendEmail메서드를 실행해아함") {
                coVerify { emailSendService.sendEmail(testEmail) }
            }
        }
    }
})