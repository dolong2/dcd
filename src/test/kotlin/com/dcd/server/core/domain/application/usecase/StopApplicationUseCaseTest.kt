package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.AlreadyStoppedException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coVerify
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class StopApplicationUseCaseTest(
    private val stopApplicationUseCase: StopApplicationUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandUserPort: CommandUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandApplicationPort: CommandApplicationPort
) : BehaviorSpec({
    val targetApplicationId = "testApplicationId"

    beforeSpec {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(id = targetApplicationId, workspace = workspace, status = ApplicationStatus.RUNNING)

        commandUserPort.save(user)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 Id가 주어지고") {
        val applicationId = "testApplicationId"
        val user = UserGenerator.generateUser()
        val application = ApplicationGenerator.generateApplication(id = applicationId, workspace = WorkspaceGenerator.generateWorkspace(user = user), status = ApplicationStatus.RUNNING)
        `when`("유스케이스가 오류없이 동작할때") {
            every { queryApplicationPort.findById(applicationId) } returns application
            stopApplicationUseCase.execute(applicationId)
            then("deleteContainerService와 deleteApplicationDirectoryService가 실행되어야함") {
                coVerify { stopContainerService.stopContainer(application) }
                verify { changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.PENDING) }
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