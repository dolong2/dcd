package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

class RunApplicationUseCaseTest : BehaviorSpec({
    val runContainerService = mockk<RunContainerService>(relaxUnitFun = true)
    val queryApplicationPort = mockk<QueryApplicationPort>(relaxUnitFun = true)
    val changeApplicationStatusService = mockk<ChangeApplicationStatusService>(relaxUnitFun = true)
    val workspaceInfo = WorkspaceInfo()
    val runApplicationUseCase = RunApplicationUseCase(
        runContainerService,
        queryApplicationPort,
        changeApplicationStatusService,
        workspaceInfo
    )

    val user = UserGenerator.generateUser()
    val workspace = WorkspaceGenerator.generateWorkspace(user = user)
    given("spring boot application, runApplicationDto가 주어지고") {
        val application = ApplicationGenerator.generateApplication(workspace = workspace)
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById("testId") } returns application
            runApplicationUseCase.execute("testId")
            then("애플리케이션 실행에 관한 service들이 실행되어야함") {
                verify { changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.PENDING) }
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

        `when`("해당 애플리케이션이 이미 실행된 있는 상태일때") {
            every { queryApplicationPort.findById("testId") } returns application.copy(status = ApplicationStatus.RUNNING)
            then("AlreadyRunningException이 발생해야됨") {
                shouldThrow<AlreadyRunningException> {
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
                coVerify { runContainerService.runApplication(application) }
            }
        }

        `when`("해당 애플리케이션이 이미 실행된 있는 상태일때") {
            every { queryApplicationPort.findById("testId") } returns application.copy(status = ApplicationStatus.RUNNING)
            then("AlreadyRunningException이 발생해야됨") {
                shouldThrow<AlreadyRunningException> {
                    runApplicationUseCase.execute("testId")
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
                coVerify { runContainerService.runApplication(application) }
            }
        }

        `when`("해당 애플리케이션이 이미 실행된 있는 상태일때") {
            every { queryApplicationPort.findById("testId") } returns application.copy(status = ApplicationStatus.RUNNING)
            then("AlreadyRunningException이 발생해야됨") {
                shouldThrow<AlreadyRunningException> {
                    runApplicationUseCase.execute("testId")
                }
            }
        }
    }
})