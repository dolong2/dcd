package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.request.AddGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
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
class AddGlobalEnvUseCaseTest(
    private val addGlobalEnvUseCase: AddGlobalEnvUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandUserPort: CommandUserPort
) : BehaviorSpec({
    val userId = "user2"
    val targetWorkspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

    given("request가 주어지고") {
        val testWorkspaceId = "testWorkspaceId"
        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user)
        commandWorkspacePort.save(workspace)
    }

        val testEnvList = mapOf("testKey" to "testValue")
        val addGlobalEnvReqDto = AddGlobalEnvReqDto(testEnvList)

        `when`("useCase를 실행할때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user

            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId, user = user))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            addGlobalEnvUseCase.execute(testWorkspaceId, addGlobalEnvReqDto)

            then("워크스페이스의 env를 저장해야함") {
                verify { workspace.copy(globalEnv = testEnvList) }
                verify { commandWorkspacePort.save(any() as Workspace) }
            }
        }

        `when`("해당 워크스페이스가 존재하지 않을때") {
            every { queryWorkspacePort.findById(testWorkspaceId) } returns null

            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    addGlobalEnvUseCase.execute(testWorkspaceId, addGlobalEnvReqDto)
                }
            }
        }

        `when`("해당 워크스페이스의 소유자가 아닐때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user

            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    addGlobalEnvUseCase.execute(testWorkspaceId, addGlobalEnvReqDto)
                }
            }
        }
    }
})