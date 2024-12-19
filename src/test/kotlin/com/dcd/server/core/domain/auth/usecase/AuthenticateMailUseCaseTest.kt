package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.ServerApplication
import com.dcd.server.core.domain.auth.dto.request.CertificateMailReqDto
import com.dcd.server.core.domain.auth.exception.ExpiredCodeException
import com.dcd.server.core.domain.auth.exception.InvalidAuthCodeException
import com.dcd.server.core.domain.auth.exception.NotFoundAuthCodeException
import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.service.VerifyEmailAuthService
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import com.ninjasquad.springmockk.SpykBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [ServerApplication::class])
class AuthenticateMailUseCaseTest(
    private val authenticateMailUseCase: AuthenticateMailUseCase,
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort
) : BehaviorSpec({
    val targetEmail = "testEmail"
    val targetCode = "testCode"

    beforeContainer {
        val emailAuth = EmailAuth(email = targetEmail, code = targetCode)
        commandEmailAuthPort.save(emailAuth)
    }

    afterContainer {
        commandEmailAuthPort.deleteByCode(targetCode)
    }

    given("이메일, 발급받은 코드가 주어지고") {
        val request = CertificateMailReqDto(targetEmail, targetCode)

        `when`("실행할때") {
            authenticateMailUseCase.execute(request)

            then("이메일 인증 엔티티의 인증 상태가 true로 변경되어야함") {
                val expectedEmailAuth = queryEmailAuthPort.findByCode(targetCode)
                expectedEmailAuth shouldNotBe  null
                expectedEmailAuth?.certificate shouldBe true
                expectedEmailAuth?.email shouldBe targetEmail
            }
        }
    }

    given("발급받지 않은 코드가 주어지고") {
        val invalidCode = "invalidCode"
        val request = CertificateMailReqDto(targetEmail, invalidCode)

        `when`("실행할때") {

            then("InvalidAuthCodeException이 발생해야함") {
                shouldThrow<InvalidAuthCodeException> {
                    authenticateMailUseCase.execute(request)
                }
            }
        }
    }
})