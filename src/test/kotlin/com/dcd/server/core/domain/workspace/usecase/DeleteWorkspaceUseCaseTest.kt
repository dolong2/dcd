package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteWorkspaceUseCaseTest(
    private val deleteWorkspaceUseCase: DeleteWorkspaceUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryUserPort: QueryUserPort
) : BehaviorSpec({
    val userId = "user1"
    val workspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("워크스페이스 아이디를 가진 워크스페이스가 주어지고") {
        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = workspaceId, user = user)
        commandWorkspacePort.save(workspace)

        `when`("유스케이스를 실행하면") {
            deleteWorkspaceUseCase.execute(workspaceId)

            then("워크스페이스가 조회되지 않아야함") {
                queryWorkspacePort.findById(workspaceId) shouldBe null
            }
        }

        `when`("해당 id를 가진 workspace가 없을때") {
            every { queryWorkspacePort.findById(workspaceId) } returns null

            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    deleteWorkspaceUseCase.execute(workspaceId)
                }
            }
        }

    }
})