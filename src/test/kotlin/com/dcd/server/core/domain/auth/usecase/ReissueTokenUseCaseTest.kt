package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.ExpiredRefreshTokenException
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.infrastructure.global.jwt.exception.TokenTypeNotValidException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import com.dcd.server.infrastructure.test.user.UserGenerator
import java.time.LocalDateTime

class ReissueTokenUseCaseTest : BehaviorSpec({
    val queryRefreshTokenPort = mockk<QueryRefreshTokenPort>()
    val commandRefreshTokenPort = mockk<CommandRefreshTokenPort>()
    val jwtPort = mockk<JwtPort>()
    val queryUserPort = mockk<QueryUserPort>()
    val parseTokenAdapter = mockk<ParseTokenAdapter>(relaxUnitFun = true)
    val reissueTokenUseCase =
        ReissueTokenUseCase(queryRefreshTokenPort, commandRefreshTokenPort, jwtPort, queryUserPort, parseTokenAdapter)

    val userId = "testUserId"
    val token = "testRefreshToken"
    val ttl = 1L
    val user = UserGenerator.generateUser()
    val tokenResDto = TokenResDto("newAccessToken", LocalDateTime.of(2023, 9, 7, 8, 30), "newRefreshToken", LocalDateTime.of(2023, 9, 7, 8, 30))

    given("리프레시 토큰이 주어지고") {
        val refreshToken = RefreshToken(userId, token, ttl)

        `when`("아무 문제 없이 실행될때") {
            every { queryRefreshTokenPort.findByToken(token) } returns refreshToken
            every { queryUserPort.findById(userId) } returns user
            every { commandRefreshTokenPort.delete(refreshToken) } returns Unit
            every { jwtPort.generateToken(user.id) } returns tokenResDto
            every { parseTokenAdapter.getJwtType(token) } returns ParseTokenAdapter.JwtPrefix.REFRESH

            val result = reissueTokenUseCase.execute(token)
            then("jwtPort에서 생성한 dto가 반환되어야함") {
                verify { queryRefreshTokenPort.findByToken(token) }
                verify { queryUserPort.findById(userId) }
                verify { commandRefreshTokenPort.delete(refreshToken) }
                result shouldBe tokenResDto
            }
        }

        `when`("토큰을 찾지 못했을때") {
            every { queryRefreshTokenPort.findByToken(token) } returns null
            then("ExpiredRefreshTokenException이 발생해야함") {
                shouldThrow<ExpiredRefreshTokenException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }

        `when`("토큰에 있는 유저가 없을때") {
            every { queryRefreshTokenPort.findByToken(token) } returns refreshToken
            every { queryUserPort.findById(userId) } returns null
            then("UserNotFoundException이 발생해야함") {
                shouldThrow<UserNotFoundException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }

        `when`("해당 토큰이 REFRESH 타입이 아닐때") {
            every { queryRefreshTokenPort.findByToken(token) } returns refreshToken
            every { queryUserPort.findById(userId) } returns user
            every { commandRefreshTokenPort.delete(refreshToken) } returns Unit
            every { jwtPort.generateToken(user.id) } returns tokenResDto
            every { parseTokenAdapter.getJwtType(token) } returns "ACCESS"
            then("TokenTypeNotValidException이 발생해야함") {
                shouldThrow<TokenTypeNotValidException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }
    }
})