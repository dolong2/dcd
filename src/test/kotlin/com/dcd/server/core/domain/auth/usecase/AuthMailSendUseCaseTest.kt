package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.ServerApplication
import com.dcd.server.core.domain.auth.dto.request.EmailSendReqDto
import com.dcd.server.core.domain.auth.service.EmailSendService
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import jakarta.mail.internet.MimeMessage
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [ServerApplication::class])
class AuthMailSendUseCaseTest(
    private val authMailSendUseCase: AuthMailSendUseCase,
    @MockkBean(relaxUnitFun = true)
    private val emailSendService: EmailSendService
) : BehaviorSpec({

    given("EmailSendRequestData가 주어지고") {
        val testEmail = "testEmail"
        val request = EmailSendReqDto(testEmail)
        `when`("execute메서드를 실행할때") {
            authMailSendUseCase.execute(request)
            then("emailSendService의 sendEmail메서드를 실행해아함") {
                coVerify { emailSendService.sendEmail(testEmail) }
            }
        }
    }
})