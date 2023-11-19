package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResponseDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
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
            workspace = workspace
        )
        val applicationList = listOf(application)
        `when`("usecase를 실행할때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryApplicationPort.findAllByWorkspace(workspace) } returns applicationList
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            val result = getAllApplicationUseCase.execute(workspace.id)
            val target = ApplicationListResponseDto(applicationList.map { it.toDto() })
            then("result는 target이랑 같아야함") {
                result shouldBe target
            }
        }
    }
})