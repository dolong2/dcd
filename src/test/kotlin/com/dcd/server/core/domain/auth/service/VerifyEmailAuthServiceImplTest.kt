package com.dcd.server.core.domain.auth.service

import com.dcd.server.core.domain.auth.exception.ExpiredCodeException
import com.dcd.server.core.domain.auth.exception.InvalidAuthCodeException
import com.dcd.server.core.domain.auth.service.impl.VerifyEmailAuthServiceImpl
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk

class VerifyEmailAuthServiceImplTest : BehaviorSpec({
    val queryEmailAuthPort = mockk<QueryEmailAuthPort>()
    val commandEmailAuthPort = mockk<CommandEmailAuthPort>()
    val serviceImpl = VerifyEmailAuthServiceImpl(queryEmailAuthPort, commandEmailAuthPort)

    given("이메일과 코드가 주어질때") {
        val testEmail = "testEmail"
        val testCode = "testCode"
        `when`("verifyCode메서드를 실행할때") {
            every { queryEmailAuthPort.existsByCodeAndEmail(testEmail, testCode) } returns false
            every { queryEmailAuthPort.existsByEmail(testEmail) } returns false
            then("코드가 만료되었다면 ExpiredEmailAuthCodeException이 throw되야함") {
                shouldThrow<ExpiredCodeException> {
                    serviceImpl.verifyCode(testEmail, testCode)
                }
            }

            every { queryEmailAuthPort.existsByEmail(testEmail) } returns true
            then("코드가 옳바르지 않으면 InvalidAuthCodeException이 throw되야함") {
                shouldThrow<InvalidAuthCodeException> {
                    serviceImpl.verifyCode(testEmail, testCode)
                }
            }
        }
    }
})