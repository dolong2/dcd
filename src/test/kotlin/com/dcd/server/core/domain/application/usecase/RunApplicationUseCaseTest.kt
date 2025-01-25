package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coVerify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RunApplicationUseCaseTest(
    private val runApplicationUseCase: RunApplicationUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandUserPort: CommandUserPort
) : BehaviorSpec({
    val targetApplicationId = "testApplicationId"

    beforeSpec {
        println("test")
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(id = targetApplicationId, workspace = workspace)

        commandUserPort.save(user)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
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