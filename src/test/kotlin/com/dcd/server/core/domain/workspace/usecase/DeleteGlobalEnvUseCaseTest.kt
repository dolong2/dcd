package com.dcd.server.core.domain.workspace.usecase

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
class DeleteGlobalEnvUseCaseTest(
    private val deleteGlobalEnvUseCase: DeleteGlobalEnvUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandUserPort: CommandUserPort
) : BehaviorSpec({
    val userId = "user2"
    val targetWorkspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken

        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user, globalEnv = mapOf("testEnvKey" to "test"))
        commandWorkspacePort.save(workspace)
    }

    given("삭제할 환경변수의 키값이 주어지고") {
        val key = "testEnvKey"

        `when`("유스케이스를 실행할때") {
            deleteGlobalEnvUseCase.execute(targetWorkspaceId, key)

            then("해당 키를 가진 전역 환경변수가 삭제되야함") {
                val result = queryWorkspacePort.findById(targetWorkspaceId)
                result shouldNotBe null
                result?.globalEnv?.get(key) shouldBe null
            }
        }

        `when`("해당 워크스페이스가 존재하고, 해당 환경변수가 존재하지 않을때") {
            val user = queryUserPort.findById(userId)!!
            val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user)
            commandWorkspacePort.save(workspace)

            then("GlobalEnvNotFoundException이 발생해야함") {
                shouldThrow<GlobalEnvNotFoundException> {
                    deleteGlobalEnvUseCase.execute(targetWorkspaceId, key)
                }
            }
        }

        `when`("해당 워크스페이스의 유저가 로그인된 유저가 아닐때") {
            val user = UserGenerator.generateUser()
            commandUserPort.save(user)
            val userDetails = authDetailsService.loadUserByUsername(user.id)
            val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authenticationToken

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    deleteGlobalEnvUseCase.execute(targetWorkspaceId, key)
                }
            }
        }
    }

    given("워크스페이스가 존재하지 않을때") {
        val notFoundWorkspaceId = "notFoundWorkspaceId"
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