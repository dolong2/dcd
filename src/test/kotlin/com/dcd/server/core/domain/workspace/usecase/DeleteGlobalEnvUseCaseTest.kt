package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
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
class DeleteGlobalEnvUseCaseTest(
    private val deleteGlobalEnvUseCase: DeleteGlobalEnvUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandUserPort: CommandUserPort,
    private val queryGlobalEnvPort: QueryGlobalEnvPort,
    private val commandGlobalEnvPort: CommandGlobalEnvPort
) : BehaviorSpec({
    val userId = "1e1973eb-3fb9-47ac-9342-c16cd63ffc6f"
    val targetWorkspaceId = "d57b42f5-5cc4-440b-8dce-b4fc2e372eff"
    val targetGlobalEnvId = UUID.randomUUID()

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user)
        val globalEnv = GlobalEnv(id = targetGlobalEnvId, key = "testEnvKey", value = "testValue", encryption = false)
        commandWorkspacePort.save(workspace)
        commandGlobalEnvPort.save(globalEnv, workspace)
    }

    given("삭제할 환경변수의 키값이 주어지고") {
        val key = "testEnvKey"

        `when`("유스케이스를 실행할때") {
            deleteGlobalEnvUseCase.execute(targetWorkspaceId, key)

            then("해당 키를 가진 전역 환경변수가 삭제되야함") {
                val targetWorkspace = queryWorkspacePort.findById(targetWorkspaceId)

                val globalEnv = queryGlobalEnvPort.findByKeyAndWorkspace(key, targetWorkspace!!)
                globalEnv shouldBe null
            }
        }

        `when`("해당 워크스페이스가 존재하고, 해당 환경변수가 존재하지 않을때") {
            val notFoundEnvKey = "nfEnvKey"

            then("GlobalEnvNotFoundException이 발생해야함") {
                shouldThrow<GlobalEnvNotFoundException> {
                    deleteGlobalEnvUseCase.execute(targetWorkspaceId, notFoundEnvKey)
                }
            }
        }
    }

    given("워크스페이스가 존재하지 않을때") {
        val notFoundWorkspaceId = UUID.randomUUID().toString()
        val key = "testEnvKey"

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    deleteGlobalEnvUseCase.execute(notFoundWorkspaceId, key)
                }
            }
        }
    }
})