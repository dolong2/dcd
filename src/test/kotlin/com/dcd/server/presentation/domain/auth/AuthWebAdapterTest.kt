package com.dcd.server.presentation.domain.auth

import com.dcd.server.core.domain.auth.usecase.AuthMailSendUseCase
import com.dcd.server.core.domain.auth.usecase.SignUpUseCase
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest
import com.dcd.server.presentation.domain.auth.data.request.SignUpRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class AuthWebAdapterTest : BehaviorSpec({
    val authMailSendUseCase = mockk<AuthMailSendUseCase>()
    val signUpUseCase = mockk<SignUpUseCase>()
    val authWebAdapter = AuthWebAdapter(authMailSendUseCase, signUpUseCase)

    given("EmailSendRequest가 주어지고") {
        val testEmail = "testEmail"
        val request = EmailSendRequest(testEmail)

        `when`("sendEmail 메서드를 실행할때") {
            every { authMailSendUseCase.execute(any()) } returns Unit
            val result = authWebAdapter.sendAuthEmail(request)
            then("문제가 없다면 usecase가 실행되어야함") {
                verify { authMailSendUseCase.execute(any()) }
                result shouldBe ResponseEntity.ok().build()
            }
        }
    }

    given("SignUpRequest가 주어지고") {
        val testEmail = "testEmail"
        val testName = "testName"
        val testPassword = "testPassword"
        val request = SignUpRequest(email = testEmail, name = testName, password = testPassword)
        `when`("signup 메서드를 실행할때") {
            every { signUpUseCase.execute(any()) } returns Unit
            val result = authWebAdapter.signupUser(request)
            then("문제가 없다면 usecase가 실행되어야함") {
                verify { signUpUseCase.execute(any()) }
                result shouldBe ResponseEntity(HttpStatus.CREATED)
            }
        }
    }
})