package com.dcd.server.presentation.domain.auth

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.core.domain.auth.usecase.*
import com.dcd.server.presentation.domain.auth.data.exetension.toDto
import com.dcd.server.presentation.domain.auth.data.exetension.toReissueResponse
import com.dcd.server.presentation.domain.auth.data.exetension.toResponse
import com.dcd.server.presentation.domain.auth.data.request.*
import com.dcd.server.presentation.domain.user.data.request.PasswordChangeRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

class AuthWebAdapterTest : BehaviorSpec({
    val authMailSendUseCase = mockk<AuthMailSendUseCase>()
    val signUpUseCase = mockk<SignUpUseCase>()
    val authenticateMailUseCase = mockk<AuthenticateMailUseCase>()
    val signInUseCase = mockk<SignInUseCase>()
    val reissueTokenUseCase = mockk<ReissueTokenUseCase>()
    val signOutUseCase = mockk<SignOutUseCase>()
    val nonAuthChangePasswordUseCase = mockk<NonAuthChangePasswordUseCase>(relaxUnitFun = true)
    val authWebAdapter = AuthWebAdapter(
        authMailSendUseCase,
        signUpUseCase,
        authenticateMailUseCase,
        signInUseCase,
        reissueTokenUseCase,
        signOutUseCase,
        nonAuthChangePasswordUseCase
    )

    given("EmailSendRequest가 주어지고") {
        val testEmail = "testEmail"
        val request = EmailSendRequest(testEmail, EmailAuthUsage.SIGNUP)

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

    given("CertificateMailRequest가 주어지고") {
        val testEmail = "testEmail"
        val testCode = "testCode"
        val request = CertificateMailRequest(testEmail, testCode)
        `when`("certificateMail 메서드를 실행할때") {
            every { authenticateMailUseCase.execute(any()) } returns Unit
            val result = authWebAdapter.certificateEmail(request)
            then("문제가 없다면 usecase가 실행되어야함") {
                verify { authenticateMailUseCase.execute(any()) }
                result shouldBe ResponseEntity.ok().build()
            }
        }
    }

    given("SignInRequest가 주어지고") {
        val testEmail = "testEmail"
        val testPassword = "testPassword"
        val request = SignInRequest(testEmail, testPassword)
        `when`("SignIn메서드를 실행할때") {
            val targetResponse = TokenResDto("testToken", LocalDateTime.now(), "refreshToken", LocalDateTime.now())
            every { signInUseCase.execute(any()) } returns targetResponse
            val result = authWebAdapter.signIn(request)
            then("상태코드는 200이여야 되고 SignInResponse가 바디에 들어있어야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body shouldBe targetResponse.toResponse()
            }
        }
    }

    given("RefreshToken이 주어지고") {
        val refreshToken = "testRefreshToken"
        `when`("reissueToken 메서드를 실행할때") {
            val responseDto =
                TokenResDto("testToken", LocalDateTime.now(), "newRefreshToken", LocalDateTime.now())
            every { reissueTokenUseCase.execute(refreshToken) } returns responseDto
            val result = authWebAdapter.reissueToken(refreshToken)
            then("상태코드는 200이여야되고 ReissuTokenResponse가 바디에 들어있어야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body shouldBe responseDto.toReissueResponse()
            }
        }
    }

    given("PasswordChangeRequest가 주어지고") {
        val nonAuthChangePasswordRequest = NonAuthChangePasswordRequest(email = "email", newPassword = "newPassword")

        `when`("changePassword 메서드를 실행할때") {
            val result = authWebAdapter.changePassword(nonAuthChangePasswordRequest)

            then("상태코드는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }

    given("주어지는게 없고") {
        `when`("signOut메서드를 실행할때") {
            every { signOutUseCase.execute("testAccessToken") } returns Unit
            val result = authWebAdapter.signOut("testAccessToken")
            then("상태코드는 200이여야함") {
                result.statusCode shouldBe HttpStatus.OK
            }
        }
    }
})