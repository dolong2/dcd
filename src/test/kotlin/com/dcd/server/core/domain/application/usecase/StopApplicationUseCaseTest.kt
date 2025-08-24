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
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
    val targetApplicationId = UUID.randomUUID().toString()

    beforeSpec {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(id = targetApplicationId, workspace = workspace, status = ApplicationStatus.RUNNING)

        commandUserPort.save(user)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 Id가 주어지고") {

        `when`("유스케이스를 실행할때") {
            stopApplicationUseCase.execute(targetApplicationId)

            then("컨테이너를 정지시켜야함") {
                val targetApplication = queryApplicationPort.findById(targetApplicationId)
                targetApplication shouldNotBe null
                targetApplication!!.status shouldBe ApplicationStatus.PENDING

                coVerify { commandPort.executeShellCommand("docker stop ${targetApplication.containerName}") }
            }
        }
    }

    given("애플리케이션이 정지되어있고") {
        val target = queryApplicationPort.findById(targetApplicationId)!!
        commandApplicationPort.save(target.copy(status = ApplicationStatus.STOPPED))

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<AlreadyStoppedException> {
                    stopApplicationUseCase.execute(targetApplicationId)
                }
            }
        }
    }

    given("존재하지 않는 애플리케이션이 주어지고") {
        val notFoundApplicationId = UUID.randomUUID().toString()

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    stopApplicationUseCase.execute(notFoundApplicationId)
                }
            }
        }
    }
})