package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import util.user.UserGenerator
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
class UpdateGlobalEnvUseCaseTest(
    private val updateGlobalEnvUseCase: UpdateGlobalEnvUseCase,
    private val authDetailsService: AuthDetailsService,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandUserPort: CommandUserPort,
    private val queryGlobalEnvPort: QueryGlobalEnvPort,
    private val commandGlobalEnvPort: CommandGlobalEnvPort
) : BehaviorSpec({
    val userId = "923a6407-a5f8-4e1e-bffd-0621910ddfc8"
    val targetWorkspaceId = "d57b42f5-5cc4-440b-8dce-b4fc2e372eff"
    val targetGlobalEnvId = UUID.randomUUID()

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        val workspace = queryWorkspacePort.findById(targetWorkspaceId)!!
        val globalEnv = GlobalEnv(id = targetGlobalEnvId, key = "testEnvKey", value = "testValue", encryption = false)
        commandGlobalEnvPort.save(globalEnv, workspace)
    }

    given("workspaceId, envKey, updateGlobalEnvReqDto가 주어지고") {
        val envKey = "testEnvKey"
        val updateGlobalEnvReqDto = UpdateGlobalEnvReqDto(newValue = "updatedValue")

        `when`("유스케이스가 예외없이 실행할때") {
            updateGlobalEnvUseCase.execute(targetWorkspaceId, envKey, updateGlobalEnvReqDto)

            then("해당 env를 수정후 저장해야함") {
                val targetWorkspace = queryWorkspacePort.findById(targetWorkspaceId)!!

                val globalEnv = queryGlobalEnvPort.findByKeyAndWorkspace(envKey, targetWorkspace)
                globalEnv shouldNotBe null
                globalEnv!!.value shouldBe updateGlobalEnvReqDto.newValue
            }
        }

        `when`("해당 env가 존재하지 않을때") {
            val notFoundEnvKey = "nofFoundEnvKey"

            then("GlobalEnvNotFoundException이 발생해야함") {
                shouldThrow<GlobalEnvNotFoundException> {
                    updateGlobalEnvUseCase.execute(targetWorkspaceId, notFoundEnvKey, updateGlobalEnvReqDto)
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
    }

    given("워크스페이스가 존재하지 않을때") {
        val notFoundWorkspaceId = UUID.randomUUID().toString()
        val envKey = "testEnvKey"
        val updateGlobalEnvReqDto = UpdateGlobalEnvReqDto(newValue = "updatedValue")

        `when`("유스케이스를 실행하면") {

            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    updateGlobalEnvUseCase.execute(notFoundWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }
    }
})