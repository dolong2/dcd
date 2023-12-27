package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class UpdateWorkspaceUseCaseTest : BehaviorSpec({
    val commandWorkspacePort = mockk<CommandWorkspacePort>(relaxUnitFun = true)
    val queryWorkspacePort = mockk<QueryWorkspacePort>(relaxUnitFun = true)
    val getCurrentUserService = mockk<GetCurrentUserService>(relaxUnitFun = true)
    val updateWorkspaceUseCase = UpdateWorkspaceUseCase(commandWorkspacePort, queryWorkspacePort, getCurrentUserService)

    given("워크스페이스 아이디가 주어지고") {
        val workspaceId = UUID.randomUUID().toString()

        `when`("해당 아이디를 가진 워크스페이스가 있을때") {
            val user =
                User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
            val workspace = Workspace(
                id = workspaceId,
                title = "workspace",
                description = "test workspace",
                owner = user
            )

            every { queryWorkspacePort.findById(workspaceId) } returns workspace
            every { getCurrentUserService.getCurrentUser() } returns user

            updateWorkspaceUseCase.execute(workspaceId, UpdateWorkspaceReqDto(title = "test title", description = "test description"))
            then("commandWorkspacePort의 save 메서드가 실행되어야함") {
                verify { commandWorkspacePort.save(any() as Workspace) }
            }
        }
    }
})