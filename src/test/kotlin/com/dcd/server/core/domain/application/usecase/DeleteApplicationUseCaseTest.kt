package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeleteApplicationException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.service.DeleteImageService
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import java.lang.RuntimeException
import java.util.*

class DeleteApplicationUseCaseTest : BehaviorSpec({
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val deleteContainerService = mockk<DeleteContainerService>(relaxUnitFun = true)
    val deleteImageService = mockk<DeleteImageService>(relaxUnitFun = true)
    val deleteApplicationUseCase =
        DeleteApplicationUseCase(commandApplicationPort, queryApplicationPort, deleteContainerService, deleteImageService)
    given("애플리케이션 id가 주어지고") {
        val applicationId = "testId"
        val user = UserGenerator.generateUser()
        val application = ApplicationGenerator.generateApplication(workspace = WorkspaceGenerator.generateWorkspace(user = user))
        `when`("usecase를 실행할때") {
            every { commandApplicationPort.delete(application) } returns Unit
            every { queryApplicationPort.findById(applicationId) } returns application
            deleteApplicationUseCase.execute(applicationId)
            then("commandApplicationPort의 delete메서드가 실행되어야함") {
                verify { commandApplicationPort.delete(application) }
                coVerify { deleteContainerService.deleteContainer(application) }
                coVerify { deleteImageService.deleteImage(application) }
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
            then("RuntimeException이 발생해야함") {
                shouldThrow<RuntimeException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
    }

    given("이미 실행중인 애플리케이션이 주어지고") {
        val applicationId = "testId"
        val user = UserGenerator.generateUser()
        val application = ApplicationGenerator.generateApplication(workspace = WorkspaceGenerator.generateWorkspace(user = user), status = ApplicationStatus.RUNNING)
        `when`("usecase를 실행할때") {
            every { commandApplicationPort.delete(application) } returns Unit
            every { queryApplicationPort.findById(applicationId) } returns application
            then("commandApplicationPort의 delete메서드가 실행되어야함") {
                shouldThrow<CanNotDeleteApplicationException> {
                    deleteApplicationUseCase.execute(applicationId)
                }
            }
        }
    }
})