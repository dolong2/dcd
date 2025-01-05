package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetWorkspaceUseCaseTest(
    private val getWorkspaceUseCase: GetWorkspaceUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort
) : BehaviorSpec({
    val userId = "user1"
    val workspaceId = "testWorkspaceId"

    beforeContainer {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("workspace가 주어지고") {
        val user = queryUserPort.findById(userId)!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = workspaceId, user = user)
        commandWorkspacePort.save(workspace)

        `when`("해당 id를 가진 workspace가 있을때") {
            val result = getWorkspaceUseCase.execute(workspaceId)

            then("result는 워크스페이스의 정보를 담고 있어야함") {
                result.id shouldBe workspaceId
                result.title shouldBe workspace.title
                result.description shouldBe workspace.description
                result.owner shouldBe workspace.owner.toDto()
                result.applicationList.isEmpty() shouldBe true
            }
        }
    }

    given("workspace가 주어지지 않고") {

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    getWorkspaceUseCase.execute(workspaceId)
                }
            }
        }
    }
})