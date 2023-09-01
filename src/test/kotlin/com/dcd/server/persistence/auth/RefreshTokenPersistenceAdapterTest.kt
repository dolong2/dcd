package com.dcd.server.persistence.auth

import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.persistence.auth.adapter.toEntity
import com.dcd.server.persistence.auth.entity.RefreshTokenEntity
import com.dcd.server.persistence.auth.repository.RefreshTokenRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.*
import org.springframework.data.repository.findByIdOrNull

class RefreshTokenPersistenceAdapterTest : BehaviorSpec({
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val adapter = RefreshTokenPersistenceAdapter(refreshTokenRepository)

    given("RefreshToken을 주어지고") {
        val refreshToken = RefreshToken("testUserId", "testToken", 1)

        `when`("save 메서드를 실행할 때") {
            every { refreshTokenRepository.save(any()) } answers{ callOriginal() }

            then("refreshTokenRepository.save가 실행되어야 함") {
                adapter.save(refreshToken)
                verify { refreshTokenRepository.save(any()) }
            }
        }

        `when`("findByToken 메서드를 실행해서 refreshTokenEntity가 조회되면") {
            every { refreshTokenRepository.findByIdOrNull(refreshToken.token) } returns refreshToken.toEntity()
            val result = adapter.findByToken(refreshToken.token)
            then("RefreshToken이 조회되어야함") {
                result shouldBe refreshToken
            }
        }

        `when`("findByToken 메서드를 실행해서 null이 조회되면") {
            every { refreshTokenRepository.findByIdOrNull("token") } returns null
            val result = adapter.findByToken("token")
            then("null이 반환되어야함") {
                result shouldBe null
            }
        }

        `when`("findByUserId 메서드를 실행하면") {
            every { refreshTokenRepository.findByUserId(refreshToken.userId) } returns listOf(refreshToken.toEntity())
            val result = adapter.findByUserId(refreshToken.userId)
            then("RefreshToken타입의 List가 반환되어야함") {
                result shouldBe listOf(refreshToken)
            }
        }
    }
})