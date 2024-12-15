package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.ServerApplication
import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.domain.auth.dto.request.SignInReqDto
import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import util.TestInitializer
import java.time.LocalDateTime

@ActiveProfiles("test")
@Import(TestInitializer::class)
@SpringBootTest(classes = [ServerApplication::class])
class SignInUseCaseTest(
    private val signInUseCase: SignInUseCase,
    @MockkBean
    private val jwtPort: JwtPort
) : BehaviorSpec({

    given("이메일이 주어지고") {
        val testEmail = "testEmail"

        `when`("올바른 패스워드로 실행할때") {
            val accessTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)
            val refreshTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)
            val accessToken = "testAccessToken"
            val refreshToken = "testRefreshToken"

            val targetTokenResDto = TokenResDto(accessToken, accessTokenExp, refreshToken, refreshTokenExp)

            val testPassword = "testPassword"
            val requestDto = SignInReqDto(testEmail, testPassword)

            every { jwtPort.generateToken("user2") } returns targetTokenResDto
            val result = signInUseCase.execute(requestDto)

            then("아무 이상이 없으면 주어진 responseDto를 반환해야됨") {
                result.accessToken shouldBe targetTokenResDto.accessToken
                result.refreshToken shouldBe targetTokenResDto.refreshToken
                result.accessTokenExp shouldBe targetTokenResDto.accessTokenExp
                result.refreshTokenExp shouldBe targetTokenResDto.refreshTokenExp
            }
        }

        `when`("패스워드가 옳바르지 않을때") {
            val testPassword = "wrongPassword"
            val requestDto = SignInReqDto(testEmail, testPassword)

            then("PasswordNotCorrectException을 던져야함") {
                shouldThrow<PasswordNotCorrectException> {
                    signInUseCase.execute(requestDto)
                }
            }
        }
    }

    given("존재하지 않는 이메일이 주어지고") {
        val testEmail = "notFoundEmail"
        val testPassword = "password"

        `when`("usecase를 실행할때") {
            val requestDto = SignInReqDto(testEmail, testPassword)

            then("해당 유저가 없을때 UserNotFoundException을 반환해야함") {
                shouldThrow<UserNotFoundException> {
                    signInUseCase.execute(requestDto)
                }
            }
        }
    }
})