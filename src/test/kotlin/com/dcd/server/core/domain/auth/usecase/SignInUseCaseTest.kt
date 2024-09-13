package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.domain.auth.dto.request.SignInReqDto
import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import util.user.UserGenerator
import java.time.LocalDateTime

class SignInUseCaseTest : BehaviorSpec({
    val jwtPort = mockk<JwtPort>()
    val securityService = mockk<SecurityService>()
    val queryUserPort = mockk<QueryUserPort>()
    val signInUseCase = SignInUseCase(queryUserPort, securityService, jwtPort)

    given("dto가 주어지고") {
        val testEmail = "testEmail"
        val testPassword = "testPassword"
        val requestDto = SignInReqDto(testEmail, testPassword)

        val user = UserGenerator.generateUser()
        val accessTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)
        val refreshTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)
        val accessToken = "testAccessToken"
        val refreshToken = "testRefreshToken"

        val tokenResDto = TokenResDto(accessToken, accessTokenExp, refreshToken, refreshTokenExp)
        `when`("usecase를 실행할때") {
            every { jwtPort.generateToken(user.id) } returns tokenResDto
            every { queryUserPort.findByEmail(requestDto.email) } returns user
            every { securityService.matchPassword(requestDto.password, user.password) } returns Unit
            val result = signInUseCase.execute(requestDto)
            then("아무 이상이 없으면 주어진 responseDto를 반환해야됨") {
                result shouldBe tokenResDto
            }

            every { securityService.matchPassword(requestDto.password, user.password) } throws PasswordNotCorrectException()
            then("패스워드가 옳바르지 않으면 PasswordNotCorrectException을 던져야함") {
                shouldThrow<PasswordNotCorrectException> {
                    signInUseCase.execute(requestDto)
                }
            }

            every { queryUserPort.findByEmail(testEmail) } returns null
            then("해당 유저가 없을때 UserNotFoundException을 반환해야함") {
                shouldThrow<UserNotFoundException> {
                    signInUseCase.execute(requestDto)
                }
            }
        }
    }
})