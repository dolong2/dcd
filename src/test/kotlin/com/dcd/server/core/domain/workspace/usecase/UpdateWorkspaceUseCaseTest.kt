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
import java.util.UUID

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
    val userId = "923a6407-a5f8-4e1e-bffd-0621910ddfc8"
    val workspaceId = "d57b42f5-5cc4-440b-8dce-b4fc2e372eff"

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
    }

    given("UpdateWorkspaceReqDto만 주어지고") {
        val notFoundWorkspaceId = UUID.randomUUID().toString()
        val request = UpdateWorkspaceReqDto(title = "test title", description = "test description")

        `when`("유스케이스를 실행하면") {
            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    updateWorkspaceUseCase.execute(notFoundWorkspaceId, request)
                }
            }
        }
    }
})