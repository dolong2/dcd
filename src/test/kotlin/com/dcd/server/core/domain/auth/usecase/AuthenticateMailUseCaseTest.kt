package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.request.CertificateMailRequestDto
import com.dcd.server.core.domain.auth.service.VerifyEmailAuthService
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthenticateMailUseCaseTest : BehaviorSpec({
    val verifyEmailAuthService = mockk<VerifyEmailAuthService>()
    val useCase = AuthenticateMailUseCase(verifyEmailAuthService)

    given("CertificateMailRequestDto가 주어지고") {
        val testEmail = "testEmail"
        val testCode = "testCode"
        val request = CertificateMailRequestDto(testEmail, testCode)

        `when`("실행할때") {
            every { verifyEmailAuthService.verifyCode(request.email, request.code) } returns Unit
            useCase.execute(request)
            then("verifyEmailAuthService의 verifyCode가 실행되어야함") {
                verify { verifyEmailAuthService.verifyCode(testEmail, testCode) }
            }
        }
    }
})