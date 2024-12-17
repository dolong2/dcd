package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.ServerApplication
import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.QueryRefreshTokenPort
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import util.TestInitializer

@ActiveProfiles("test")
@Import(TestInitializer::class)
@SpringBootTest(classes = [ServerApplication::class])
class SignOutUseCaseTest(
    private val signOutUseCase: SignOutUseCase,
    @MockkBean
    private val securityService: SecurityService,
    private val commandRefreshTokenPort: CommandRefreshTokenPort,
    private val queryRefreshTokenPort: QueryRefreshTokenPort
) : BehaviorSpec({

    val targetUserId = "user1"

    beforeContainer {
        every { securityService.getCurrentUserId() } returns targetUserId
        val refreshToken = RefreshToken(userId = targetUserId, token = "testToken", refreshTTL = 10L)
        commandRefreshTokenPort.save(refreshToken)
    }

    afterContainer {
        commandRefreshTokenPort.delete(queryRefreshTokenPort.findByUserId(targetUserId))
    }

    given("targetUserId가 주어지고") {
        `when`("useCase를 실행할때") {
            signOutUseCase.execute()
            then("targetUserId를 가진 refreshToken이 없어야함") {
                queryRefreshTokenPort.findByUserId(targetUserId).isEmpty() shouldBe true
            }
        }

    }
})