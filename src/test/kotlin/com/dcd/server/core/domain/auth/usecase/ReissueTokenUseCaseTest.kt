package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.exception.ExpiredRefreshTokenException
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.GenerateTokenPort
import com.dcd.server.core.domain.auth.spi.ParseTokenPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.infrastructure.global.jwt.adapter.JwtPrefix
import com.dcd.server.infrastructure.global.jwt.exception.TokenTypeNotValidException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ReissueTokenUseCaseTest(
    private val queryRefreshTokenPort: QueryRefreshTokenPort,
    private val refreshTokenPort: CommandRefreshTokenPort,
    private val queryUserPort: QueryUserPort
) : BehaviorSpec({
    val generateTokenPort: GenerateTokenPort = mockk<GenerateTokenPort>()
    val parseTokenPort: ParseTokenPort = mockk<ParseTokenPort>()
    val reissueTokenUseCase = ReissueTokenUseCase(queryRefreshTokenPort, refreshTokenPort, generateTokenPort, parseTokenPort, queryUserPort)

    val userId = "1e1973eb-3fb9-47ac-9342-c16cd63ffc6f"
    val token = "testRefreshToken"
    val ttl = 10000L

    given("리프레시 토큰이 주어지고") {
        val expectedAccessToken = "accessToken"
        val refreshToken = RefreshToken(userId, token, ttl)
        val accessTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)
        val refreshTokenExp = LocalDateTime.of(2023, 9, 5, 8, 3)

        val targetTokenResDto = TokenResDto(expectedAccessToken, accessTokenExp, token, refreshTokenExp)


        `when`("아무 문제 없이 실행될때") {
            every { parseTokenPort.getJwtType(token) } returns JwtPrefix.REFRESH
            every { generateTokenPort.generateToken("1e1973eb-3fb9-47ac-9342-c16cd63ffc6f") } returns targetTokenResDto
            refreshTokenPort.save(refreshToken)

            val result = reissueTokenUseCase.execute(token)
            then("jwtPort에서 생성한 dto가 반환되어야함") {
                result shouldBe targetTokenResDto
            }
        }

        `when`("토큰을 찾지 못했을때") {
            then("ExpiredRefreshTokenException이 발생해야함") {
                shouldThrow<ExpiredRefreshTokenException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }

        `when`("토큰에 있는 유저가 없을때") {
            val notFoundToken = refreshToken.copy(userId = UUID.randomUUID().toString(), token = "notFoundRefreshToken", ttl)
            refreshTokenPort.save(notFoundToken)
            every { parseTokenPort.getJwtType(notFoundToken.token) } returns JwtPrefix.REFRESH

            then("UserNotFoundException이 발생해야함") {
                shouldThrow<UserNotFoundException> {
                    reissueTokenUseCase.execute(notFoundToken.token)
                }
            }
        }

        `when`("해당 토큰이 REFRESH 타입이 아닐때") {
            every { parseTokenPort.getJwtType(token) } returns JwtPrefix.ACCESS

            then("TokenTypeNotValidException이 발생해야함") {
                shouldThrow<TokenTypeNotValidException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }
    }
})