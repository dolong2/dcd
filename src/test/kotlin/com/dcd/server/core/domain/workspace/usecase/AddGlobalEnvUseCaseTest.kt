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

        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user)
        commandWorkspacePort.save(workspace)
    }

    given("추가할 env가 주어지고") {
        val testEnvList = mapOf("testKey" to "testValue")
        val addGlobalEnvReqDto = AddGlobalEnvReqDto(testEnvList)

        `when`("useCase를 실행할때") {
            addGlobalEnvUseCase.execute(targetWorkspaceId, addGlobalEnvReqDto)

            then("워크스페이스의 env를 저장해야함") {
                val resultWorkspace = queryWorkspacePort.findById(targetWorkspaceId)

                resultWorkspace shouldNotBe null
                val resultGlobalEnv = resultWorkspace?.globalEnv
                resultGlobalEnv?.get("testKey") shouldBe "testValue"
            }
        }

        `when`("해당 워크스페이스의 소유자가 아닐때") {
            val generateUser = UserGenerator.generateUser()
            commandUserPort.save(generateUser)
            val updatedWorkspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = generateUser)
            commandWorkspacePort.save(updatedWorkspace)

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    addGlobalEnvUseCase.execute(targetWorkspaceId, addGlobalEnvReqDto)
                }
            }
        }
    }
})