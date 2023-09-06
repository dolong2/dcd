package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.response.TokenResponseDto
import com.dcd.server.core.domain.auth.exception.ExpiredRefreshTokenException
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class ReissueTokenUseCaseTest : BehaviorSpec({
    val queryRefreshTokenPort = mockk<QueryRefreshTokenPort>()
    val commandRefreshTokenPort = mockk<CommandRefreshTokenPort>()
    val jwtPort = mockk<JwtPort>()
    val queryUserPort = mockk<QueryUserPort>()
    val reissueTokenUseCase =
        ReissueTokenUseCase(queryRefreshTokenPort, commandRefreshTokenPort, jwtPort, queryUserPort)

    val userId = "testUserId"
    val token = "testRefreshToken"
    val ttl = 1L
    val user =
        User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
    val tokenResponseDto = TokenResponseDto("newAccessToken", LocalDateTime.of(2023, 9, 7, 8, 30), "newRefreshToken", LocalDateTime.of(2023, 9, 7, 8, 30))

    given("리프레시 토큰이 주어지고") {
        val refreshToken = RefreshToken(userId, token, ttl)

        fun init(){
            every { queryRefreshTokenPort.findByToken(token) } returns refreshToken
            every { queryUserPort.findById(userId) } returns user
            every { commandRefreshTokenPort.delete(refreshToken) } returns Unit
            every { jwtPort.generateToken(user.id, user.roles) } returns tokenResponseDto
        }

        init()
        `when`("아무 문제 없이 실행될때") {
            val result = reissueTokenUseCase.execute(token)
            then("jwtPort에서 생성한 dto가 반환되어야함") {
                verify { queryRefreshTokenPort.findByToken(token) }
                verify { queryUserPort.findById(userId) }
                verify { commandRefreshTokenPort.delete(refreshToken) }
                result shouldBe tokenResponseDto
            }
        }

        every { queryRefreshTokenPort.findByToken(token) } returns null
        `when`("토큰을 찾지 못했을때") {
            then("ExpiredRefreshTokenException이 발생해야함") {
                shouldThrow<ExpiredRefreshTokenException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }

        init()
        every { queryUserPort.findById(userId) } returns null
        `when`("토큰에 있는 유저가 없을때") {
            then("UserNotFoundException이 발생해야함") {
                shouldThrow<UserNotFoundException> {
                    reissueTokenUseCase.execute(token)
                }
            }
        }
    }
})