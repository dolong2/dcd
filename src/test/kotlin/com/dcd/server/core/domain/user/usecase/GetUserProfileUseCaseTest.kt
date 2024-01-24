package com.dcd.server.core.domain.user.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toProfileDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.user.usecase.GetUserProfileUseCase
import com.dcd.server.core.domain.workspace.dto.extension.toProfileDto
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class GetUserProfileUseCaseTest : BehaviorSpec({
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val queryApplicationPort = mockk<QueryApplicationPort>()

    val getUserProfileUseCase = GetUserProfileUseCase(getCurrentUserService, queryWorkspacePort, queryApplicationPort)

    given("유저, 애플리케이션, 워크스페이스가 주어지고") {
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val workspace = Workspace(
            UUID.randomUUID().toString(),
            title = "test workspace",
            description = "test workspace description",
            owner = user
        )
        val application = Application(
            id = "testId",
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            version = "17",
            workspace = workspace,
            port = 8080
        )

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
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))

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