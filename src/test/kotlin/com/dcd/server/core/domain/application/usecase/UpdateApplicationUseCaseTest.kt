package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class UpdateApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandApplicationPort = mockk<CommandApplicationPort>(relaxUnitFun = true)
    val getCurrentUserService = mockk<GetCurrentUserService>()

    val updateApplicationUseCase =
        UpdateApplicationUseCase(queryApplicationPort, commandApplicationPort, getCurrentUserService)

    val user =
        User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
    val workspace = Workspace(
        UUID.randomUUID().toString(),
        title = "test workspace",
        description = "test workspace description",
        owner = user
    )
    val applicationId = "testId"
    val updateReqDto = UpdateApplicationReqDto(name = "updated application", description = "dldl", version = "11")

    given("애플리케이션이 주어지고") {
        val application = Application(
            id = applicationId,
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            version = "17",
            workspace = workspace,
            port = 8080
        )

        `when`("usecase를 실행할때") {
            every { getCurrentUserService.getCurrentUser() } returns user
            every { queryApplicationPort.findById(applicationId) } returns application

            updateApplicationUseCase.execute(applicationId, updateReqDto)

            then("ReqDto의 내용이 반영된 애플리케이션을 저장해야함") {
                val updatedApplication = application.copy(name = updateReqDto.name, description = updateReqDto.description, version = updateReqDto.version)
                verify { commandApplicationPort.save(updatedApplication) }
            }
        }

        `when`("로그인된 유저가 workspace 주인이 아닐때") {
            every { getCurrentUserService.getCurrentUser() } returns user.copy(id = "another", name = "another", email = "another")

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    updateApplicationUseCase.execute(applicationId, updateReqDto)
                }
            }
        }
    }

    given("애플리케이션이 주어지지 않고") {

        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    updateApplicationUseCase.execute(applicationId, updateReqDto)
                }
            }
        }
    }
})