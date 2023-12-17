package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class DeleteWorkspaceUseCaseTest : BehaviorSpec({
    val commandWorkspacePort = mockk<CommandWorkspacePort>(relaxUnitFun = true)
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val deleteWorkspaceUseCase = DeleteWorkspaceUseCase(commandWorkspacePort, queryWorkspacePort, getCurrentUserService)

    given("workspaceId가 주어지고") {
        val workspaceId = UUID.randomUUID().toString()

        `when`("해당 id를 가진 workspace가 있을때") {
            val user =
                User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
            val workspace = Workspace(
                id = workspaceId,
                title = "workspace",
                description = "test workspace",
                owner = user
            )

            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryWorkspacePort.findById(workspaceId) } returns workspace

            deleteWorkspaceUseCase.execute(workspaceId)
            then("delete 메서드를 호출해야함") {
                verify { commandWorkspacePort.delete(workspace) }
            }
        }

    }
})