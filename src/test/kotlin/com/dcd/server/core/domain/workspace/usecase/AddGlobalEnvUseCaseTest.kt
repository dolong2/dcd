package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.request.AddGlobalEnvReqDto
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
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator

class AddGlobalEnvUseCaseTest : BehaviorSpec({
    val queryWorkspacePort = mockk<QueryWorkspacePort>(relaxUnitFun = true)
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val commandWorkspacePort = mockk<CommandWorkspacePort>(relaxUnitFun = true)
    val addGlobalEnvUseCase = AddGlobalEnvUseCase(queryWorkspacePort, getCurrentUserService, commandWorkspacePort)

    given("request가 주어지고") {
        val testWorkspaceId = "testWorkspaceId"
        val testEnvList = mapOf("testKey" to "testValue")
        val addGlobalEnvReqDto = AddGlobalEnvReqDto(testEnvList)

        `when`("useCase를 실행할때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user

            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId, user = user))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            addGlobalEnvUseCase.execute(testWorkspaceId, addGlobalEnvReqDto)

            then("워크스페이스의 env를 저장해야함") {
                verify { workspace.copy(globalEnv = testEnvList) }
                verify { commandWorkspacePort.save(any() as Workspace) }
            }
        }

        `when`("해당 워크스페이스가 존재하지 않을때") {
            every { queryWorkspacePort.findById(testWorkspaceId) } returns null

            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    addGlobalEnvUseCase.execute(testWorkspaceId, addGlobalEnvReqDto)
                }
            }
        }

        `when`("해당 워크스페이스의 소유자가 아닐때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user

            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    addGlobalEnvUseCase.execute(testWorkspaceId, addGlobalEnvReqDto)
                }
            }
        }
    }
})