package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.service.impl.ValidateWorkspaceOwnerServiceImpl
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ValidateWorkspaceOwnerServiceTest(
    private val validateWorkspaceOwnerServiceImpl: ValidateWorkspaceOwnerServiceImpl,
    private val commandUserPort: CommandUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val authDetailsService: AuthDetailsService
) : BehaviorSpec({
    val owner = UserGenerator.generateUser()
    val otherUser = UserGenerator.generateUser()
    val workspace = WorkspaceGenerator.generateWorkspace(user = owner)

    beforeContainer {
        commandUserPort.save(owner)
        commandUserPort.save(otherUser)
        commandWorkspacePort.save(workspace)
    }

    given("워크스페이스 소유주와 워크스페이스가 주어지고") {

        `when`("validateOwner 메서드를 실행하면") {
            val result = validateWorkspaceOwnerServiceImpl.validateOwner(owner, workspace)

            then("결과값은 Unit이여야됨") {
                result shouldBe Unit
            }
        }
    }

    given("워크스페이스 소유주가 아닌 유저와 워크스페이스가 주어지고") {

        `when`("validateOwner 메서드를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    validateWorkspaceOwnerServiceImpl.validateOwner(otherUser, workspace)
                }
            }
        }
    }

    given("시큐리티 컨텍스트에 소유자가 주어지고") {
        val userDetails = authDetailsService.loadUserByUsername(owner.id)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        `when`("validateOwner 메서드를 실행하면") {
            val result = validateWorkspaceOwnerServiceImpl.validateOwner(workspace)

            then("결과값은 Unit이여야함") {
                result shouldBe Unit
            }
        }
    }

    given("시큐리티 컨텍스트에 소유자가 아닌 유저가 주어지고") {
        val userDetails = authDetailsService.loadUserByUsername(otherUser.id)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        `when`("validateOwner 메서드를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    validateWorkspaceOwnerServiceImpl.validateOwner(workspace)
                }
            }
        }
    }
})