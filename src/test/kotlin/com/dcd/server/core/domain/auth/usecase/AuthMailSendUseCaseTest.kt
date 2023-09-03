package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.request.EmailSendRequestData
import com.dcd.server.core.domain.auth.service.EmailSendService
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthMailSendUseCaseTest : BehaviorSpec({
    val emailSendService = mockk<EmailSendService>()
    val useCase = AuthMailSendUseCase(emailSendService)

    given("EmailSendRequestData가 주어지고") {
        val testEmail = "testEmail"
        val request = EmailSendRequestData(testEmail)
        `when`("execute메서드를 실행할때") {
            every { emailSendService.sendEmail(testEmail) } returns Unit
            useCase.execute(request)
            then("emailSendService의 sendEmail메서드를 실행해아함") {
                verify { emailSendService.sendEmail(testEmail) }
            }
        }
    }
})