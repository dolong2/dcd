package com.dcd.server.infrastructure.global.security

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class EncryptAdapterTest(
    private val encryptAdapter: EncryptAdapter
) : BehaviorSpec({
    given("평문이 주어지고") {
        val plainText = "Hello World"

        `when`("평문을 암호화했을때") {
            val encrypt = encryptAdapter.encrypt(plainText)

            then("암호화된 스트링은 평문이랑 일치하지 않아야함") {
                encrypt shouldNotBe plainText
            }

            then("복호화하면 평문이 나와야됨") {
                val decrypt = encryptAdapter.decrypt(encrypt)
                decrypt shouldBe plainText
            }
        }
    }
})