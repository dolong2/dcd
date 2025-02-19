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
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
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
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(id = targetApplicationId, workspace = workspace)

        commandUserPort.save(user)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 아이디가 주어지고") {

        `when`("유스케이스를 실행할때") {
            runApplicationUseCase.execute(targetApplicationId)

            then("타겟 애플리케이션 상태가 보류로 변경되고 컨테이너를 실행해야함") {
                val targetApplication = queryApplicationPort.findById(targetApplicationId)
                targetApplication shouldNotBe null
                targetApplication!!.status shouldBe ApplicationStatus.PENDING

                coVerify { commandPort.executeShellCommand("docker start ${targetApplication.containerName}") }
            }
        }
    }

    given("이미 애플리케이션이 실행중이고") {
        val target = queryApplicationPort.findById(targetApplicationId)!!
        commandApplicationPort.save(target.copy(status = ApplicationStatus.RUNNING))

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<AlreadyRunningException> {
                    runApplicationUseCase.execute(targetApplicationId)
                }
            }
        }
    }

    given("존재하지 않는 애플리케이션 아이디가 주어지고") {
        val notFoundApplicationId = "notFoundApplicationId"

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    runApplicationUseCase.execute(notFoundApplicationId)
                }
            }
        }
    }
})