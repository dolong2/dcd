package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.aop.exception.NotCertificateEmailException
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.exception.AlreadyExistsUserException
import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.persistence.auth.repository.EmailAuthRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class SignupUseCaseTest(
    private val signUpUseCase: SignUpUseCase,
    private val commandEmailAuthPort: CommandEmailAuthPort,
    private val emailAuthRepository: EmailAuthRepository,
    private val queryUserPort: QueryUserPort,
    private val passwordEncoder: PasswordEncoder,
    private val commandUserPort: CommandUserPort
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
        commandUserPort.delete(queryUserPort.findByEmail(targetEmail)!!)
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

        `when`("같은 유저가 존재할때") {
            val emailAuth = EmailAuth(
                email = testEmail,
                certificate = true
            )
            commandEmailAuthPort.save(emailAuth)
            val request = SignUpReqDto(testEmail, testPassword, testName)

            then("AlreadyExistsUserException이 발생해야함") {
                shouldThrow<AlreadyExistsUserException> {
                    signUpUseCase.execute(request)
                }
            }
        }

        `when`("같은 유저가 없을때 실행") {
            val request = SignUpReqDto(targetEmail, testPassword, testName)
            signUpUseCase.execute(request)
            then("commandPort의 save메서드를 실행해야함") {
                val result = queryUserPort.findByEmail(targetEmail)
                result shouldNotBe null
                result?.email shouldBe targetEmail
                result?.name shouldBe testName
                passwordEncoder.matches(request.password, result?.password) shouldBe true
            }
        }
    }
})