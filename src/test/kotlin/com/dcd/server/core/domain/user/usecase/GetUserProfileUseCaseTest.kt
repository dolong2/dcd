package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toProfileDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.dto.extension.toProfileDto
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetUserProfileUseCaseTest(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val authDetailsService: AuthDetailsService,
    private val queryUserPort: QueryUserPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) : BehaviorSpec({
    val userId = "user1"
    beforeTest {
        val userDetails = authDetailsService.loadUserByUsername(userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
        SecurityContextHolder.getContext().authentication = authenticationToken
    }

    given("유저, 애플리케이션, 워크스페이스가 주어지고") {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)

        `when`("usecase를 실행할때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryWorkspacePort.findByUser(user) } returns listOf(workspace)
            every { queryApplicationPort.findAllByWorkspace(workspace) } returns listOf(application)

            val result = getUserProfileUseCase.execute()

            then("워크스페이스와 애플리케이션을 조회해야함") {
                verify { queryApplicationPort.findAllByWorkspace(workspace) }
                verify { queryWorkspacePort.findByUser(user) }
            }

            then("결과값은 user, application, workspace의 값을 담고있어야함") {
                result.user shouldBe user.toDto()
                result.workspaces shouldBe listOf(workspace.toProfileDto(listOf(application.toProfileDto())))
            }
        }
    }

    given("유저가 주어지고") {
        val user = UserGenerator.generateUser()

        `when`("usecase를 실행할때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryWorkspacePort.findByUser(user) } returns listOf()

            val result = getUserProfileUseCase.execute()

            then("워크스페이스를 조회해야함") {
                verify { queryWorkspacePort.findByUser(user) }
            }

            then("결과값은 user의 값을 담고 있어야함") {
                result.user shouldBe user.toDto()
                result.workspaces shouldBe listOf()
            }
        }
    }
})