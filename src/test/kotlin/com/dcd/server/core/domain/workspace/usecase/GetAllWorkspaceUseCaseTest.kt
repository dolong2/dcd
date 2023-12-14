package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.util.*

class GetAllWorkspaceUseCaseTest : BehaviorSpec({
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val getAllWorkspaceUseCase = GetAllWorkspaceUseCase(getCurrentUserService, queryWorkspacePort, queryApplicationPort)

    given("workspaceId, workspace, workspaceList, user가 주어지고") {
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val firstWorkspaceId = UUID.randomUUID().toString()
        val firstWorkspace = Workspace(
            id = firstWorkspaceId,
            title = "workspace",
            description = "test workspace",
            owner = user
        )
        val secondWorkspaceId = UUID.randomUUID().toString()
        val secondWorkspace = Workspace(
            id = secondWorkspaceId,
            title = "workspace",
            description = "test workspace",
            owner = user
        )
        val workspaceList = listOf(firstWorkspace, secondWorkspace)

        `when`("해당 user가 workspace를 여러개 가지고 있을때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryWorkspacePort.findByUser(user) } returns workspaceList
            every { queryApplicationPort.findAllByWorkspace(firstWorkspace) } returns listOf()
            every { queryApplicationPort.findAllByWorkspace(secondWorkspace) } returns listOf()

            val result = getAllWorkspaceUseCase.execute()
            then("주어진 workspace가 전부 반환되어야함") {
                val firstWorkspaceResult = result.list[0]
                firstWorkspaceResult.id shouldBe firstWorkspaceId
                firstWorkspaceResult.title shouldBe firstWorkspace.title
                firstWorkspaceResult.description shouldBe firstWorkspace.description
                firstWorkspaceResult.owner shouldBe firstWorkspace.owner.toDto()
                firstWorkspaceResult.applicationList.isEmpty() shouldBe true
                val secondWorkspaceResult = result.list[1]
                secondWorkspaceResult.id shouldBe secondWorkspaceId
                secondWorkspaceResult.title shouldBe secondWorkspace.title
                secondWorkspaceResult.description shouldBe secondWorkspace.description
                secondWorkspaceResult.owner shouldBe secondWorkspace.owner.toDto()
                secondWorkspaceResult.applicationList.isEmpty() shouldBe true
            }
        }
    }

})