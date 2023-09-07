package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SignOutUseCaseTest : BehaviorSpec({
    val commandRefreshTokenPort = mockk<CommandRefreshTokenPort>()
    val queryRefreshTokenPort = mockk<QueryRefreshTokenPort>()
    val securityService = mockk<SecurityService>()
    val signOutUseCase = SignOutUseCase(commandRefreshTokenPort, queryRefreshTokenPort, securityService)

    given("userId, RefreshToken이 주어지고") {
        val userId = "testUserId"
        val testToken = "testToken"
        val refreshToken = RefreshToken(userId = userId, token = testToken, refreshTTL = 10L)
        `when`("useCase를 실행할때") {
            val tokenList = listOf(refreshToken)
            every { queryRefreshTokenPort.findByUserId(userId) } returns tokenList
            every { commandRefreshTokenPort.delete(tokenList) } returns Unit
            every { securityService.getCurrentUserId() } returns userId
            signOutUseCase.execute()
            then("commandRefreshTokenPort의 delete메서드를 실행해야함") {
                verify { commandRefreshTokenPort.delete(tokenList) }
            }
        }

    }
})