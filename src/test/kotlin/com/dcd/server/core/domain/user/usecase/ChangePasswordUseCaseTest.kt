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
        val userId = "1e1973eb-3fb9-47ac-9342-c16cd63ffc6f"
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("User, PasswordChangeReqDto가 주어지고") {
        val passwordChangeReqDto = PasswordChangeReqDto(
            existingPassword = "testPassword",
            newPassword = "newPassword"
        )

        `when`("usecase를 실행할때") {
            changePasswordUseCase.execute(passwordChangeReqDto)
            then("passwordChangeReqDto의 새 패스워드를 가진 유저를 저장해야함") {
                val user = queryUserPort.findById("1e1973eb-3fb9-47ac-9342-c16cd63ffc6f")
                user shouldNotBe null
                passwordEncoder.matches(passwordChangeReqDto.newPassword, user?.password)
            }
        }

        `when`("password가 일치하지 않을때") {

            then("PasswordNotCorrectException이 발생해야함") {
                shouldThrow<PasswordNotCorrectException> {
                    changePasswordUseCase.execute(passwordChangeReqDto)
                }
            }
        }
    }
})