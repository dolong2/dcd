package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.exception.AlreadyExistsUserException
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SignupUseCaseTest : BehaviorSpec({
    val queryUserPort = mockk<QueryUserPort>()
    val commandUserPort = mockk<CommandUserPort>()
    val securityService = mockk<SecurityService>()
    val signUpUseCase = SignUpUseCase(securityService, commandUserPort, queryUserPort)

    given("signupRequest가 주어지고") {
        val testEmail = "testEmail"
        val testName = "testName"
        val testPassword = "testPassword"
        val request = SignUpReqDto(testEmail, testPassword, testName)
        `when`("이미 같은 유저가 있을때 실행") {
            every { queryUserPort.existsByEmail(request.email) } returns true
            then("AlreadyExistsUserException이 발생해야함") {
                shouldThrow<AlreadyExistsUserException> {
                    signUpUseCase.execute(request)
                }
            }
        }

        `when`("같은 유저가 없을때 실행") {
            every { queryUserPort.existsByEmail(request.email) } returns false
            every { securityService.encodePassword(request.password) } returns "encodedPassword"
            every { commandUserPort.save(any()) } answers { callOriginal() }
            signUpUseCase.execute(request)
            then("commandPort의 save메서드를 실행해야함") {
                verify { commandUserPort.save(any()) }
            }
        }
    }
})