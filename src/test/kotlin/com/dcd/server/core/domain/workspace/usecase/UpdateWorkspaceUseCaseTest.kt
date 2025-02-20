package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UpdateWorkspaceUseCaseTest(
    private val updateWorkspaceUseCase: UpdateWorkspaceUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryUserPort: QueryUserPort,
    private val commandUserPort: CommandUserPort,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort
) : BehaviorSpec({
    val userId = "user1"
    val workspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("워크스페이스, UpdateWorkspaceReqDto가 주어지고") {
        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = workspaceId, user = user)
        commandWorkspacePort.save(workspace)

        val request = UpdateWorkspaceReqDto(title = "test title", description = "test description")

        `when`("유스케이스를 실행할때") {
            updateWorkspaceUseCase.execute(workspaceId, request)

            then("워크스페이스의 이름과 설명이 변경되어야함") {
                val result = queryWorkspacePort.findById(workspaceId)
                result shouldNotBe null
                result?.title shouldBe request.title
                result?.description shouldBe request.description
            }
        }

        `when`("워크스페이스의 유저와 로그인된 유저가 다를때") {
            val otherUser = UserGenerator.generateUser()
            commandUserPort.save(otherUser)
            val userDetails = authDetailsService.loadUserByUsername(otherUser.id)
            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authenticationToken


            then("WorkspaceOwnerNotSameException") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    updateWorkspaceUseCase.execute(workspaceId, request)
                }
            }
        }

    }

    given("UpdateWorkspaceReqDto만 주어지고") {
        val request = UpdateWorkspaceReqDto(title = "test title", description = "test description")

        `when`("유스케이스를 실행하면") {
            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    updateWorkspaceUseCase.execute(workspaceId, request)
                }
            }
        }
    }
})