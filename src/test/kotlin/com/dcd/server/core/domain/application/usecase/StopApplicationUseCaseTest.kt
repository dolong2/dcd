package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.DeleteContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class StopApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val deleteContainerService = mockk<DeleteContainerService>()
    val deleteApplicationDirectoryService = mockk<DeleteApplicationDirectoryService>()
    val stopApplicationUseCase =
        StopApplicationUseCase(queryApplicationPort, deleteContainerService, deleteApplicationDirectoryService)

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
            owner = user
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