package com.dcd.server.core.common.service

import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.common.service.impl.SecurityServiceImpl
import com.dcd.server.core.common.spi.SecurityPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SecurityServiceImplTest : BehaviorSpec({
    val securityPort = mockk<SecurityPort>()
    val securityServiceImpl = SecurityServiceImpl(securityPort)

    given("현재 유저 id가 주어지고") {
        val testUserId = "testUserId"
        every { securityPort.getCurrentUserId() } returns testUserId
        `when`("securityPort에서 주어진 id를 반환할때") {
            val result = securityServiceImpl.getCurrentUserId()
            then("결과값은 반환된 유저 id여야함") {
                result shouldBe testUserId
            }
        }
    }

    given("rawPassword가 주어지고") {
        val rawPassword = "rawPassword"
        val encodedPassword = "encodedPassword"
        every { securityPort.encodeRawPassword(rawPassword) } returns encodedPassword
        `when`("securityPort에서 인코딩된 패스워드를 반환할때") {
            val result = securityServiceImpl.encodePassword(rawPassword)
            then("결과값은 encodedPassword여야함") {
                result shouldBe encodedPassword
            }
        }
    }

    given("rawPassword와 encodedPassword가 주어지고") {
        val rawPassword = "rawPassword"
        val encodedPassword = "encodedPassword"
        every { securityPort.isCorrectPassword(rawPassword, encodedPassword) } returns true
        `when`("securityPort에서 true를 반환하면") {
            val result = securityServiceImpl.matchPassword(rawPassword, encodedPassword)
            then("결과값은 Unit이여야함") {
                result shouldBe Unit
            }
        }
        every { securityPort.isCorrectPassword(rawPassword, encodedPassword) } returns false
        `when`("securityPort에서 false를 반환하면") {
            then("PasswordNotCorrectException이 발생해야함") {
                shouldThrow<PasswordNotCorrectException> {
                    securityServiceImpl.matchPassword(rawPassword, encodedPassword)
                }
            }
        }
    }
})