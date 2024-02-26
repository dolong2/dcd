package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.response.RunApplicationResDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
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
import java.util.*

class RunApplicationUseCaseTest : BehaviorSpec({
    val cloneApplicationByUrlService = mockk<CloneApplicationByUrlService>(relaxUnitFun = true)
    val modifyGradleService = mockk<ModifyGradleService>(relaxUnitFun = true)
    val createDockerFileService = mockk<CreateDockerFileService>(relaxUnitFun = true)
    val buildDockerImageService = mockk<BuildDockerImageService>(relaxUnitFun = true)
    val dockerRunService = mockk<DockerRunService>(relaxUnitFun = true)
    val queryApplicationPort = mockk<QueryApplicationPort>(relaxUnitFun = true)
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val getExternalPortService = mockk<GetExternalPortService>(relaxed = true)
    val changeApplicationStatusService = mockk<ChangeApplicationStatusService>(relaxUnitFun = true)
    val runApplicationUseCase = RunApplicationUseCase(
        cloneApplicationByUrlService,
        modifyGradleService,
        createDockerFileService,
        buildDockerImageService,
        dockerRunService,
        queryApplicationPort,
        validateWorkspaceOwnerService,
        getExternalPortService,
        changeApplicationStatusService
    )

    val user = UserGenerator.generateUser()
    val workspace = WorkspaceGenerator.generateWorkspace(user = user)
    given("spring boot application, runApplicationDto가 주어지고") {
        val application = ApplicationGenerator.generateApplication(workspace = workspace)
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById("testId") } returns application
            val result = runApplicationUseCase.execute("testId")
            then("애플리케이션 실행에 관한 service들이 실행되어야함") {
                verify { cloneApplicationByUrlService.cloneByApplication(application) }
                verify { validateWorkspaceOwnerService.validateOwner(workspace) }
                verify { modifyGradleService.modifyGradleByApplication(application) }
                verify { createDockerFileService.createFileToApplication(application, application.version, 0) }
                verify { buildDockerImageService.buildImageByApplication(application) }
                verify { dockerRunService.runApplication(application, getExternalPortService.getExternalPort(application.port)) }
                verify { changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING) }
            }
            then("반환값은 이용가능한 외부 포트를 담아야함") {
                result shouldBe RunApplicationResDto(getExternalPortService.getExternalPort(application.port))
            }
        }

        `when`("애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById("testId") } returns null
            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    runApplicationUseCase.execute("testId")
                }
            }
        }
    }

    given("mysql application, runApplicationReqDto가 주어지고") {
        val application = ApplicationGenerator.generateApplication(workspace = workspace, applicationType = ApplicationType.MYSQL)

        `when`("usecase를 실행하면") {
            every { queryApplicationPort.findById(application.id) } returns application

            runApplicationUseCase.execute(application.id)
            then("dockerRunService만 실행되어야함") {
                verify { dockerRunService.runApplication(application, application.version, getExternalPortService.getExternalPort(application.port)) }
                shouldThrow<AssertionError> {
                    verify { cloneApplicationByUrlService.cloneByApplication(application) }
                    verify { validateWorkspaceOwnerService.validateOwner(workspace) }
                    verify { modifyGradleService.modifyGradleByApplication(application) }
                    verify { createDockerFileService.createFileToApplication(application, application.version, 0) }
                    verify { buildDockerImageService.buildImageByApplication(application) }
                    verify { changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING) }
                }
            }
        }
    }

    given("redis application, runApplicationReqDto가 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.REDIS)

        `when`("usecase를 실행하면") {
            every { queryApplicationPort.findById(application.id) } returns application

            runApplicationUseCase.execute(application.id)
            then("dockerRunService만 실행되어야함") {
                verify { dockerRunService.runApplication(application, application.version, getExternalPortService.getExternalPort(application.port)) }
                shouldThrow<AssertionError> {
                    verify { cloneApplicationByUrlService.cloneByApplication(application) }
                    verify { validateWorkspaceOwnerService.validateOwner(workspace) }
                    verify { modifyGradleService.modifyGradleByApplication(application) }
                    verify { createDockerFileService.createFileToApplication(application, application.version, 0) }
                    verify { buildDockerImageService.buildImageByApplication(application) }
                    verify { changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING) }
                }
            }
        }
    }
})