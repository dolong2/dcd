package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class GetAllApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val getAllApplicationUseCase = GetAllApplicationUseCase(queryApplicationPort, getCurrentUserService, queryWorkspacePort)

    given("applicationList가 주어지고") {
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
            port = 8080,
            status = ApplicationStatus.STOPPED
        )
        val applicationList = listOf(application)
        `when`("usecase를 실행할때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryApplicationPort.findAllByWorkspace(workspace) } returns applicationList
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            val result = getAllApplicationUseCase.execute(workspace.id)
            val target = ApplicationListResDto(applicationList.map { it.toDto() })
            then("result는 target이랑 같아야함") {
                result shouldBe target
            }
        }

        `when`("실행한 유저가 워크스페이스의 주인이 아닐때") {
            val another =
                User(email = "another", password = "password", name = "another", roles = mutableListOf(Role.ROLE_USER))
            every { getCurrentUserService.getCurrentUser() } returns another
            every { queryApplicationPort.findAllByWorkspace(workspace) } returns applicationList
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    getAllApplicationUseCase.execute(workspace.id)
                }
            }
        }
    }
})