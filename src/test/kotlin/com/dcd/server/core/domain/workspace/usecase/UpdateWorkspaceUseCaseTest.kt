package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
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
    private val commandUserPort: CommandUserPort
) : BehaviorSpec({
    val userId = "user1"
    val workspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

        `when`("해당 아이디를 가진 워크스페이스가 있을때") {
            val user = UserGenerator.generateUser()
            val workspace = spyk(
                Workspace(
                    id = workspaceId,
                    title = "workspace",
                    description = "test workspace",
                    owner = user
                )
            )
    given("워크스페이스, UpdateWorkspaceReqDto가 주어지고") {
        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = workspaceId, user = user)
        commandWorkspacePort.save(workspace)

        val request = UpdateWorkspaceReqDto(title = "test title", description = "test description")

            updateWorkspaceUseCase.execute(workspaceId, reqDto)
            then("commandWorkspacePort의 save 메서드가 실행되어야함") {
                verify { workspace.copy(title = reqDto.title, description = reqDto.description) }
                verify { commandWorkspacePort.save(any() as Workspace) }
            }
        }

        `when`("주어진 아이디를 가진 워크스페이스가 없을때") {
            every { queryWorkspacePort.findById(workspaceId) } returns null

            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    updateWorkspaceUseCase.execute(workspaceId, reqDto)
                }
            }
        }

        `when`("워크스페이스의 유저와 로그인된 유저가 다를때") {
            val user = UserGenerator.generateUser()
            val workspace = WorkspaceGenerator.generateWorkspace(user = user)

            val anotherUser = UserGenerator.generateUser(email = "another")

            every { getCurrentUserService.getCurrentUser() } returns anotherUser
            every { queryWorkspacePort.findById(workspaceId) } returns workspace

            then("WorkspaceOwnerNotSameException") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    updateWorkspaceUseCase.execute(workspaceId, reqDto)
                }
            }
        }

    }
})