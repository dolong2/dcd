package com.dcd.server.persistence.auth

import com.dcd.server.core.domain.auth.model.EmailAuth
import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage
import com.dcd.server.persistence.auth.adapter.toEntity
import com.dcd.server.persistence.auth.repository.EmailAuthRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull

class EmailAuthPersistenceAdapterTest : BehaviorSpec({
    val emailAuthRepository = mockk<EmailAuthRepository>()
    val adapter = EmailAuthPersistenceAdapter(emailAuthRepository)

    given("이메일 인증객체가 주어지고") {
        val testCode = "testCode"
        val testEmail = "testEmail"
        val emailAuth = EmailAuth(testEmail, testCode, usage = EmailAuthUsage.SIGNUP)
        val emailAuthEntity = emailAuth.toEntity()

        `when`("save를 실행할때") {
            every { emailAuthRepository.save(any()) } answers { callOriginal() }
            adapter.save(emailAuth)
            then("repository의 save메서드가 실행되어야함") {
                verify { emailAuthRepository.save(any()) }
            }
        }

        `when`("deleteByCode를 실행할때") {
            every { emailAuthRepository.deleteById(testCode) } returns Unit
            adapter.deleteByCode(testCode)
            then("repository의 deleteById가 실행되어야함") {
                verify { emailAuthRepository.deleteById(testCode) }
            }
        }

        `when`("deleteByEmailAndCode를 실행할때") {
            every { emailAuthRepository.deleteByEmailAndCode(testEmail, testCode) } returns Unit
            adapter.deleteByEmailAndCode(testEmail, testCode)
            then("repository의 deleteByEmailAndCode가 실행되어야함") {
                verify { emailAuthRepository.deleteByEmailAndCode(testEmail, testCode) }
            }
        }

        `when`("findByEmail을 실행할때") {
            every { emailAuthRepository.findByEmail(testEmail) } returns listOf(emailAuthEntity)
            val result = adapter.findByEmail(testEmail)
            then("emailAuth가 담겨있는 list가 반환되어야함") {
                verify { emailAuthRepository.findByEmail(testEmail) }
                result shouldBe listOf(emailAuth)
            }
        }

        `when`("findByCode를 실행할때") {
            every { emailAuthRepository.findByIdOrNull(testCode) } returns emailAuthEntity
            var result = adapter.findByCode(testCode)
            then("emailAuth가 반환되어야함") {
                verify { emailAuthRepository.findByIdOrNull(testCode) }
                result shouldBe emailAuth
            }

            every { emailAuthRepository.findByIdOrNull(testCode) } returns null
            result = adapter.findByCode(testCode)
            then("repository에서 반환되는 값이 null일때 null이 반환되어야 함") {
                verify { emailAuthRepository.findByIdOrNull(testCode) }
                result shouldBe null
            }
        }
    }
})