package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.ExpiredRefreshTokenException
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.infrastructure.global.jwt.exception.TokenTypeNotValidException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ReissueTokenUseCaseTest(
    private val reissueTokenUseCase: ReissueTokenUseCase,
    @MockkBean
    private val jwtPort: JwtPort,
    @MockkBean
    private val parseTokenAdapter: ParseTokenAdapter,
    private val refreshTokenPort: CommandRefreshTokenPort
) : BehaviorSpec({
    val userId = "user2"
    val token = "testRefreshToken"
    val ttl = 10000L

    given("리프레시 토큰이 주어지고") {
        val expectedAccessToken = "accessToken"
        val refreshToken = RefreshToken(userId, token, ttl)
        val accessTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)
        val refreshTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)

        val targetTokenResDto = TokenResDto(expectedAccessToken, accessTokenExp, token, refreshTokenExp)


        `when`("아무 문제 없이 실행될때") {
            every { parseTokenAdapter.getJwtType(token) } returns ParseTokenAdapter.JwtPrefix.REFRESH
            every { jwtPort.generateToken("user2") } returns targetTokenResDto
            refreshTokenPort.save(refreshToken)

            val result = reissueTokenUseCase.execute(token)
            then("jwtPort에서 생성한 dto가 반환되어야함") {
                result shouldBe targetTokenResDto
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