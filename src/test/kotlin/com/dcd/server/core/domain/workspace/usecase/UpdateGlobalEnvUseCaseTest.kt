package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.request.UpdateGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
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
    }
})