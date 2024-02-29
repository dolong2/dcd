package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.GetContainerLogService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

class GetApplicationLogUseCaseTest : BehaviorSpec({
    val getContainerLogService = mockk<GetContainerLogService>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val getApplicationLogUseCase = GetApplicationLogUseCase(
        getContainerLogService,
        queryApplicationPort,
        getCurrentUserService,
        validateWorkspaceOwnerService
    )

    given("애플리케이션 id가 주어지고") {
        val appId = "testApplicationId"
        val owner = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = owner)
        val application = ApplicationGenerator.generateApplication(id = appId, workspace = workspace)

        `when`("해당 애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById(appId) } returns null

            then("유스케이스 실행시 ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    getApplicationLogUseCase.execute(appId)
                }
            }
        }
    }
})