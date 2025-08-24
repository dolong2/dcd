package com.dcd.server.infrastructure.global.jwt

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.infrastructure.global.jwt.adapter.GenerateTokenAdapter
import com.dcd.server.infrastructure.global.jwt.properties.JwtProperty
import com.dcd.server.infrastructure.global.jwt.properties.TokenTimeProperty
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GenerateTokenAdapterTest : BehaviorSpec({
    val commandRefreshTokenPort = mockk<CommandRefreshTokenPort>()
    val jwtProperty = JwtProperty("accessSecret123123123123123123123123123123123123123", "refreshSecret123123123123123123123123123123213123123213213123213123213123")
    val tokenTimeProperty = TokenTimeProperty(10, 100)
    val adapter = GenerateTokenAdapter(jwtProperty, tokenTimeProperty, commandRefreshTokenPort)

    given("adapter가 주어졌을때") {
        every { commandRefreshTokenPort.save(any()) } answers {callOriginal()}
        val userId = "testUserId"
        `when`("generateToken 메서드를 실행하면") {
            val response = adapter.generateToken(userId)
            then("tokenResponse의 타입으로 나와야함") {
                response::class shouldBe TokenResDto::class
            }
        }
    }

})