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

    given("user와 workspace가 주어지고") {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        `when`("현재 인증된 유저가 workspace의 주인일때") {
            val result = service.validateOwner(user, workspace)
            then("결과값은 Unit이여야됨") {
                result shouldBe Unit
            }
        }
        `when`("현재 인증된 유저가 workspace의 주인이 아닐때") {
            val another =
                User(email = "another", password = "password", name = "another user", roles = mutableListOf(Role.ROLE_USER), status = Status.CREATED)
            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    service.validateOwner(another, workspace)
                }
            }
        }
    }
})