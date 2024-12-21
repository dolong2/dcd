package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator

class UpdateApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandApplicationPort = mockk<CommandApplicationPort>(relaxUnitFun = true)

    val updateApplicationUseCase =
        UpdateApplicationUseCase(queryApplicationPort, commandApplicationPort)

    val user = UserGenerator.generateUser()
    val workspace = WorkspaceGenerator.generateWorkspace(user = user)
    val applicationId = "testId"
    val updateReqDto = UpdateApplicationReqDto(name = "updated application", description = "dldl", applicationType = ApplicationType.SPRING_BOOT, githubUrl = null, version = "11", port = 8080)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(id = applicationId, workspace = workspace)

        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns application

            updateApplicationUseCase.execute(applicationId, updateReqDto)

            then("ReqDto의 내용이 반영된 애플리케이션을 저장해야함") {
                val updatedApplication = application.copy(name = updateReqDto.name, description = updateReqDto.description, applicationType = updateReqDto.applicationType, githubUrl = updateReqDto.githubUrl, version = updateReqDto.version, port = updateReqDto.port)
                verify { commandApplicationPort.save(updatedApplication) }
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

    given("실행중인 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(id = applicationId, workspace = workspace, status = ApplicationStatus.RUNNING)

        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns application

            then("ReqDto의 내용이 반영된 애플리케이션을 저장해야함") {
                shouldThrow<AlreadyRunningException> {
                    updateApplicationUseCase.execute(applicationId, updateReqDto)
                }
            }
        }
    }
})