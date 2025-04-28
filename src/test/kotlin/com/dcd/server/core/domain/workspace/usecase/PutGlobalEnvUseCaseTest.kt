package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.request.PutGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class PutGlobalEnvUseCaseTest(
    private val putGlobalEnvUseCase: PutGlobalEnvUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandUserPort: CommandUserPort,
    private val queryGlobalEnvPort: QueryGlobalEnvPort
) : BehaviorSpec({
    val userId = "1e1973eb-3fb9-47ac-9342-c16cd63ffc6f"
    val targetWorkspaceId = "d57b42f5-5cc4-440b-8dce-b4fc2e372eff"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user)
        commandWorkspacePort.save(workspace)
    }

    given("추가할 env가 주어지고") {
        val testEnvList = listOf(PutEnvReqDto("testKey", "testValue", false))
        val putGlobalEnvReqDto = PutGlobalEnvReqDto(testEnvList)

        `when`("useCase를 실행할때") {
            putGlobalEnvUseCase.execute(targetWorkspaceId, putGlobalEnvReqDto)

            then("워크스페이스의 env를 저장해야함") {
                val resultWorkspace = queryWorkspacePort.findById(targetWorkspaceId)

                val globalEnv = queryGlobalEnvPort.findByKeyAndWorkspace("testKey", resultWorkspace!!)
                globalEnv shouldNotBe null
                globalEnv!!.value shouldBe "testValue"
            }
        }

        `when`("해당 워크스페이스의 소유자가 아닐때") {
            val generateUser = UserGenerator.generateUser()
            commandUserPort.save(generateUser)
            val updatedWorkspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = generateUser)
            commandWorkspacePort.save(updatedWorkspace)

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    putGlobalEnvUseCase.execute(targetWorkspaceId, putGlobalEnvReqDto)
                }
            }
        }
    }

    given("존재하지 않은 워크스페이스 아이디가 주어지고") {
        val givenWorkspaceId = UUID.randomUUID().toString()
        val testEnvList = listOf(PutEnvReqDto("testKey", "testValue", false))
        val putGlobalEnvReqDto = PutGlobalEnvReqDto(testEnvList)

        `when`("유스케이스를 실행할때") {

            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    putGlobalEnvUseCase.execute(givenWorkspaceId, putGlobalEnvReqDto)
                }
            }
        }
    }
})