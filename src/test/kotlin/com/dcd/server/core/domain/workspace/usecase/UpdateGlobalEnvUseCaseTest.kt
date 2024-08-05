package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
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

class UpdateGlobalEnvUseCaseTest : BehaviorSpec({
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val commandWorkspacePort = mockk<CommandWorkspacePort>(relaxUnitFun = true)
    val updateGlobalEnvUseCase = UpdateGlobalEnvUseCase(queryWorkspacePort, getCurrentUserService, commandWorkspacePort)

    given("workspaceId, envKey, updateGlobalEnvReqDto가 주어지고") {
        val testWorkspaceId = "testWorkspaceId"
        val envKey = "testEnvKey"
        val updateGlobalEnvReqDto = UpdateGlobalEnvReqDto(newValue = "updatedValue")

        `when`("유스케이스가 예외없이 실행할때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user
            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId, user = user, globalEnv = mapOf("testEnvKey" to "dcd")))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            updateGlobalEnvUseCase.execute(testWorkspaceId, envKey, updateGlobalEnvReqDto)

            then("해당 env를 수정후 저장해야함") {
                verify { workspace.copy(globalEnv = mapOf(envKey to updateGlobalEnvReqDto.newValue)) }
                verify { commandWorkspacePort.save(any() as Workspace) }
            }
        }

        `when`("해당 env가 존재하지 않을때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user
            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId, user = user))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            then("GlobalEnvNotFoundException이 발생해야함") {
                shouldThrow<GlobalEnvNotFoundException> {
                    updateGlobalEnvUseCase.execute(testWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }

        `when`("워크스페이스 소유자가 일치하지 않을때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user
            val workspace = spyk(WorkspaceGenerator.generateWorkspace(id = testWorkspaceId))
            every { queryWorkspacePort.findById(testWorkspaceId) } returns workspace

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    updateGlobalEnvUseCase.execute(testWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }

        `when`("워크스페이스가 존재하지 않을때") {
            every { queryWorkspacePort.findById(testWorkspaceId) } returns null

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    updateGlobalEnvUseCase.execute(testWorkspaceId, envKey, updateGlobalEnvReqDto)
                }
            }
        }
    }
})