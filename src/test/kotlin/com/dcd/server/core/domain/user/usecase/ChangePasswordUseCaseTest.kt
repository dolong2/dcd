package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.domain.user.dto.request.PasswordChangeReqDto
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.security.crypto.password.PasswordEncoder
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ChangePasswordUseCaseTest(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val queryUserPort: QueryUserPort,
    private val passwordEncoder: PasswordEncoder,
    private val authDetailsService: AuthDetailsService
) : BehaviorSpec({
    beforeTest {
        val userId = "user2"
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

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