package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.exception.AlreadyExistsUserException
import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.persistence.auth.repository.EmailAuthRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class SignupUseCaseTest(
    private val signUpUseCase: SignUpUseCase,
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val emailAuthRepository: EmailAuthRepository,
    private val queryUserPort: QueryUserPort
) : BehaviorSpec({

    val targetEmail = "targetEmail"

    beforeSpec {
        val emailAuth = EmailAuth(
            email = targetEmail,
            certificate = true
        )
        commandEmailAuthPort.save(emailAuth)
    }

    afterSpec {
        emailAuthRepository.deleteAll()
    }

    given("signupRequest가 주어지고") {
        val testEmail = "testEmail"
        val testName = "testName"
        val testPassword = "testPassword"

        `when`("이메일 인증을 하지 않은 유저가 실행할때") {
            val request = SignUpReqDto(testEmail, testPassword, testName)
            then("NotCertificateEmailException이 발생해야함") {
                shouldThrow<NotCertificateEmailException> {
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