package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.collections.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetApplicationLogUseCaseTest(
    private val getApplicationLogUseCase: GetApplicationLogUseCase,
    @MockkBean
    private val commandPort: CommandPort,
    private val commandUserPort: CommandUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandApplicationPort: CommandApplicationPort
) : BehaviorSpec({
    val targetApplicationId = "testApplicationId"

    beforeSpec {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(id = targetApplicationId, workspace = workspace)
        val expectedResult = listOf("test logs")
        every { commandPort.executeShellCommandWithResult("docker logs ${application.containerName}") } returns expectedResult

        commandUserPort.save(user)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 id가 주어지고") {

        `when`("유스케이스를 실행할때") {
            val result = getApplicationLogUseCase.execute(targetApplicationId)

            then("로그가 조회되어야함") {
                result.logs shouldContain "test logs"
            }
        }
    }

    given("존재하지 않는 애플리케이션 id가 주어지고") {
        val notFoundApplicationId = "notFoundApplicationId"
        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    getApplicationLogUseCase.execute(notFoundApplicationId)
                }
            }
        }
    }
})