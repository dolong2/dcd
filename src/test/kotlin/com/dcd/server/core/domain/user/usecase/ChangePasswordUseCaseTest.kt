package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.domain.user.dto.request.PasswordChangeReqDto
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.user.spi.CommandUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import com.dcd.server.infrastructure.test.user.UserGenerator

class ChangePasswordUseCaseTest : BehaviorSpec({
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val commandUserPort = mockk<CommandUserPort>(relaxUnitFun = true)
    val passwordEncoder = mockk<PasswordEncoder>()
    val changePasswordUseCase = ChangePasswordUseCase(getCurrentUserService, commandUserPort, passwordEncoder)

    given("User, PasswordChangeReqDto가 주어지고") {
        val user = UserGenerator.generateUser()
        val passwordChangeReqDto = PasswordChangeReqDto(
            existingPassword = "existingPassword",
            newPassword = "newPassword"
        )

        `when`("usecase를 실행할때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { passwordEncoder.matches(passwordChangeReqDto.existingPassword, user.password) } returns true
            every { passwordEncoder.encode(passwordChangeReqDto.newPassword) } returns passwordChangeReqDto.newPassword

            changePasswordUseCase.execute(passwordChangeReqDto)
            then("passwordChangeReqDto의 새 패스워드를 가진 유저를 저장해야함") {
                verify {
                    commandUserPort.save(user.copy(password = passwordChangeReqDto.newPassword))
                }
            }
        }

        `when`("password가 일치하지 않을때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { passwordEncoder.matches(passwordChangeReqDto.existingPassword, user.password) } returns false

            then("PasswordNotCorrectException이 발생해야함") {
                shouldThrow<PasswordNotCorrectException> {
                    changePasswordUseCase.execute(passwordChangeReqDto)
                }
            }
        }
    }
})