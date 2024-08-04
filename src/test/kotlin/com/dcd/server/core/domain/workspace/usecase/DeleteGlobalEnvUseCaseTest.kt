package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.GlobalEnvNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import java.util.*

class DeleteGlobalEnvUseCaseTest : BehaviorSpec({
    val queryWorkspacePort = mockk<QueryWorkspacePort>(relaxUnitFun = true)
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val commandWorkspacePort = mockk<CommandWorkspacePort>(relaxUnitFun = true)
    val deleteGlobalEnvUseCase = DeleteGlobalEnvUseCase(queryWorkspacePort, getCurrentUserService, commandWorkspacePort)

    given("workspaceId, 삭제할 환경변수의 키값이 주어지고") {
        val workspaceId = UUID.randomUUID().toString()
        val key = "testEnvKey"

        `when`("해당 워크스페이스가 존재하고, 해당 환경변수가 존재할때") {
            val env = mapOf(key to "testValue")
            val user = UserGenerator.generateUser()
            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = workspaceId, globalEnv = env, user = user))
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryWorkspacePort.findById(workspaceId) } returns workspace

            deleteGlobalEnvUseCase.execute(workspaceId, key)

            then("해당 키가 삭제된 워크스페이스를 저장해야함") {
                verify { workspace.copy(globalEnv = mapOf()) }
                verify { commandWorkspacePort.save(any() as Workspace) }
            }
        }

        `when`("해당 워크스페이스가 존재하고, 해당 환경변수가 존재하지 않을때") {
            val user = UserGenerator.generateUser()
            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = workspaceId, user = user))
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryWorkspacePort.findById(workspaceId) } returns workspace

            then("GlobalEnvNotFoundException이 발생해야함") {
                shouldThrow<GlobalEnvNotFoundException> {
                    deleteGlobalEnvUseCase.execute(workspaceId, key)
                }
            }
        }
    }
})