package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
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
class UpdateGlobalEnvUseCaseTest(
    private val updateGlobalEnvUseCase: UpdateGlobalEnvUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandUserPort: CommandUserPort
) : BehaviorSpec({
    val userId = "user1"
    val targetWorkspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user, globalEnv = mapOf("testEnvKey" to "test"))
        commandWorkspacePort.save(workspace)
    }

    given("workspaceId, envKey, updateGlobalEnvReqDto가 주어지고") {
        val envKey = "testEnvKey"
        val updateGlobalEnvReqDto = UpdateGlobalEnvReqDto(newValue = "updatedValue")

        `when`("유스케이스가 예외없이 실행할때") {
            updateGlobalEnvUseCase.execute(targetWorkspaceId, envKey, updateGlobalEnvReqDto)

            then("해당 env를 수정후 저장해야함") {
                val result = queryWorkspacePort.findById(targetWorkspaceId)!!

                result shouldNotBe null
                result.globalEnv[envKey] shouldBe updateGlobalEnvReqDto.newValue
            }
        }

        `when`("해당 env가 존재하지 않을때") {
            val targetWorkspace = queryWorkspacePort.findById(targetWorkspaceId)!!
            commandWorkspacePort.save(targetWorkspace.copy(globalEnv = mapOf()))

            then("GlobalEnvNotFoundException이 발생해야함") {
                shouldThrow<GlobalEnvNotFoundException> {
                    updateGlobalEnvUseCase.execute(targetWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }

        `when`("워크스페이스 소유자가 일치하지 않을때") {
            val user = UserGenerator.generateUser()
            commandUserPort.save(user)
            val userDetails = authDetailsService.loadUserByUsername(user.id)
            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authenticationToken

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    updateGlobalEnvUseCase.execute(targetWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }

        `when`("워크스페이스가 존재하지 않을때") {
            every { queryWorkspacePort.findById(testWorkspaceId) } returns null

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    updateGlobalEnvUseCase.execute(testWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }
    }
})