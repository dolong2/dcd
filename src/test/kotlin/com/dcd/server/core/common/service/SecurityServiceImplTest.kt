package com.dcd.server.core.common.service

import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.common.service.impl.SecurityServiceImpl
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class SecurityServiceImplTest(
    private val securityServiceImpl: SecurityServiceImpl,
    private val authDetailsService: AuthDetailsService,
    private val passwordEncoder: PasswordEncoder
) : BehaviorSpec({
    val targetUserId = "user2"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(targetUserId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("현재 유저 id가 주어지고") {

        `when`("현재 로그인된 유저의 아이디를 조회할때") {
            val result = securityServiceImpl.getCurrentUserId()

            then("결과값은 반환된 유저 id여야함") {
                result shouldBe targetUserId
            }
        }
    }

    given("rawPassword가 주어지고") {
        val rawPassword = "rawPassword"
        val encodedPassword = "encodedPassword"
        every { securityPort.encodeRawPassword(rawPassword) } returns encodedPassword
        `when`("securityPort에서 인코딩된 패스워드를 반환할때") {
            val result = securityServiceImpl.encodePassword(rawPassword)
            then("결과값은 encodedPassword여야함") {
                result shouldBe encodedPassword
            }
        }
    }

    given("rawPassword와 encodedPassword가 주어지고") {
        val rawPassword = "rawPassword"
        val encodedPassword = "encodedPassword"
        every { securityPort.isCorrectPassword(rawPassword, encodedPassword) } returns true
        `when`("securityPort에서 true를 반환하면") {
            val result = securityServiceImpl.matchPassword(rawPassword, encodedPassword)
            then("결과값은 Unit이여야함") {
                result shouldBe Unit
            }
        }
        every { securityPort.isCorrectPassword(rawPassword, encodedPassword) } returns false
        `when`("securityPort에서 false를 반환하면") {
            then("PasswordNotCorrectException이 발생해야함") {
                shouldThrow<PasswordNotCorrectException> {
                    securityServiceImpl.matchPassword(rawPassword, encodedPassword)
                }
            }
        }
    }
})