package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.AlreadyStoppedException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ChangeApplicationStatusService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

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
        val user = UserGenerator.generateUser()
        val application = ApplicationGenerator.generateApplication(id = applicationId, workspace = WorkspaceGenerator.generateWorkspace(user = user), status = ApplicationStatus.RUNNING)
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
        `when`("해당 애플리케이션이 이미 정지된 있는 상태일때") {
            every { queryApplicationPort.findById(applicationId) } returns application.copy(status = ApplicationStatus.STOPPED)
            then("AlreadyStoppedException이 발생해야됨") {
                shouldThrow<AlreadyStoppedException> {
                    stopApplicationUseCase.execute(applicationId)
                }
            }
        }
    }
})