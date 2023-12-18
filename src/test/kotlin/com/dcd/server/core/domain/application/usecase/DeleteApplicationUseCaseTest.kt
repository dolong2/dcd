package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.lang.RuntimeException
import java.util.*

class DeleteApplicationUseCaseTest : BehaviorSpec({
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val deleteApplicationUseCase =
        DeleteApplicationUseCase(getCurrentUserService, commandApplicationPort, queryApplicationPort, validateWorkspaceOwnerService)
    given("애플리케이션 id가 주어지고") {
        val applicationId = "testId"
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val application = Application(
            id = applicationId,
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            workspace = Workspace(UUID.randomUUID().toString(), title = "test workspace", description = "test workspace description", owner = user),
            port = 8080
        )
        every { getCurrentUserService.getCurrentUser() } returns user
        `when`("usecase를 실행할때") {
            every { commandApplicationPort.delete(application) } returns Unit
            every { queryApplicationPort.findById(applicationId) } returns application
            deleteApplicationUseCase.execute(applicationId)
            then("commandApplicationPort의 delete메서드가 실행되어야함") {
                verify { commandApplicationPort.delete(application) }
            }
        }
        `when`("application을 찾을 수 없을때") {
            every { queryApplicationPort.findById(applicationId) } returns null
            then("applicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
        `when`("현재 유저가 소유자가 아닐때") {
            every { getCurrentUserService.getCurrentUser() } returns user.copy(id = "otherUser")
            then("RuntimeException이 발생해야함") {
                shouldThrow<RuntimeException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
    }
})