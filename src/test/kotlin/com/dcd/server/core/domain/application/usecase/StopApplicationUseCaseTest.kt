package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class StopApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val deleteContainerService = mockk<DeleteContainerService>()
    val deleteApplicationDirectoryService = mockk<DeleteApplicationDirectoryService>()
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val changeApplicationStatusService = mockk<ChangeApplicationStatusService>(relaxUnitFun = true)
    val stopApplicationUseCase =
        StopApplicationUseCase(queryApplicationPort, deleteContainerService, deleteApplicationDirectoryService, validateWorkspaceOwnerService, changeApplicationStatusService)

    given("애플리케이션 Id가 주어지고") {
        val applicationId = "testApplicationId"
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val application = Application(
            id = applicationId,
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            version = "17",
            workspace = Workspace(UUID.randomUUID().toString(), title = "test workspace", description = "test workspace description", owner = user),
            port = 8080,
            status = ApplicationStatus.STOPPED
        )
        `when`("유스케이스가 오류없이 동작할때") {
            every { queryApplicationPort.findById(applicationId) } returns application
            every { deleteContainerService.deleteContainer(application) } returns Unit
            every { deleteApplicationDirectoryService.deleteApplicationDirectory(application) } returns Unit
            stopApplicationUseCase.execute(applicationId)
            then("deleteContainerService와 deleteApplicationDirectoryService가 실행되어야함") {
                verify { deleteApplicationDirectoryService.deleteApplicationDirectory(application) }
                verify { deleteContainerService.deleteContainer(application) }
            }
        }
        `when`("해당 애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById(applicationId) } returns null
            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    stopApplicationUseCase.execute(applicationId)
                }
            }
        }
    }
})