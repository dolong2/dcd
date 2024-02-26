package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.CreateNetworkService
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.user.UserGenerator

class CreateWorkspaceUseCaseTest : BehaviorSpec({
    val commandWorkspacePort = mockk<CommandWorkspacePort>(relaxed = true)
    val getCurrentUserService = mockk<GetCurrentUserService>(relaxed = true)
    val createNetworkService = mockk<CreateNetworkService>(relaxed = true)
    val createWorkspaceUseCase =
        CreateWorkspaceUseCase(commandWorkspacePort, getCurrentUserService, createNetworkService)

    given("request가 주어지고") {
        val createWorkspaceReqDto = CreateWorkspaceReqDto(
            title = "test workspace title",
            description = "test workspace description"
        )
        `when`("useCase를 실행할때") {
            val user = UserGenerator.generateUser()
            every { getCurrentUserService.getCurrentUser() } returns user
            createWorkspaceUseCase.execute(createWorkspaceReqDto)
            then("워크스페이스를 저장하고 네트워크를 생성해야함") {
                verify { getCurrentUserService.getCurrentUser() }
                verify { commandWorkspacePort.save(any() as Workspace) }
                verify { createNetworkService.createNetwork(createWorkspaceReqDto.title) }
            }
        }
    }
})