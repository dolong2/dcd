package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.domain.auth.dto.request.NonAuthChangePasswordReqDto
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.security.crypto.password.PasswordEncoder
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class NonAuthChangePasswordUseCaseTest(
    private val nonAuthChangePasswordUseCase: NonAuthChangePasswordUseCase,
    private val queryUserPort: QueryUserPort,
    private val passwordEncoder: PasswordEncoder,
    private val commandEmailAuthPort: CommandEmailAuthPort
) : BehaviorSpec({
    val targetEmail = "testEmail"

    beforeSpec {
        val emailAuth = EmailAuth(email = targetEmail, code = "testCode", certificate = true)
        commandEmailAuthPort.save(emailAuth)
    }

    afterSpec {
        commandEmailAuthPort.deleteByCode("testCode")
    }

    given("유저와 NonAuthChangePasswordReqDto가 주어지고") {

        `when`("유스케이스를 실행할때") {
            val nonAuthChangePasswordReqDto = NonAuthChangePasswordReqDto(email = targetEmail, newPassword = "newPassword")
            nonAuthChangePasswordUseCase.execute(nonAuthChangePasswordReqDto)

            then("NonAuthChangePasswordReqDto의 새 패스워드를 가진 유저를 저장해야함") {
                val result = queryUserPort.findByEmail(targetEmail)
                result shouldNotBe null
                passwordEncoder.matches(nonAuthChangePasswordReqDto.newPassword, result?.password)
            }
        }

        `when`("해당 이메일을 가진 유저가 존재하지 않을때") {
            val notFoundUserEmail = "notFoundUser"
            val nonAuthChangePasswordReqDto = NonAuthChangePasswordReqDto(email = notFoundUserEmail, newPassword = "newPassword")

            then("UserNotFoundException이 발생해야함") {
                shouldThrow<UserNotFoundException> {
                    nonAuthChangePasswordUseCase.execute(nonAuthChangePasswordReqDto)
                }
            }
        }
    }
})