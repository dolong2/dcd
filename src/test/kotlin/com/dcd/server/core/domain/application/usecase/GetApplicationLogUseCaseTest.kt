package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.GetContainerLogService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

class GetApplicationLogUseCaseTest : BehaviorSpec({
    val getContainerLogService = mockk<GetContainerLogService>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val getApplicationLogUseCase = GetApplicationLogUseCase(
        getContainerLogService,
        queryApplicationPort
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

        `when`("해당 애플리케이션이 존재하지만, 로그인된 유저가 워크스페이스의 권한을 가지고 있지 않을때") {
            val user = UserGenerator.generateUser(email = "thief")

            every { queryApplicationPort.findById(appId) } returns application

            then("유스케이스 실행시 WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    getApplicationLogUseCase.execute(appId)
                }
            }
        }

        `when`("해당 애플리케이션이 존재하고, 로그인된 유저가 워크스페이스의 권한을 가지고 있을때") {
            val logs = listOf("testLogs")

            every { queryApplicationPort.findById(appId) } returns application
            every { getContainerLogService.getLogs(application) } returns logs

            val response = getApplicationLogUseCase.execute(appId)
            then("유스케이스의 반환값은 logs를 가지고 있어야함") {
                response.logs shouldBe logs
            }
            then("유스케이스는 getContainerLogService를 실행해야함") {
                verify { getContainerLogService.getLogs(application) }
            }
        }
    }
})