package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
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


    given("애플리케이션 id가 주어지고") {
        val appId = "testApplicationId"
        val owner = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = owner)
        val application = ApplicationGenerator.generateApplication(id = appId, workspace = workspace)

        `when`("해당 애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById(appId) } returns null

            then("유스케이스 실행시 ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    getApplicationLogUseCase.execute(appId)
                }
            }
        }

        `when`("해당 애플리케이션이 존재하고, 로그인된 유저가 워크스페이스의 권한을 가지고 있을때") {
            val logs = listOf("testLogs")

            every { queryApplicationPort.findById(appId) } returns application
            every { getContainerLogService.getLogs(application) } returns logs

            val response = getApplicationLogUseCase.execute(appId)
            then("유스케이스의 반환값은 logs를 가지고 있어야함") {
                response.logs shouldBe logs
            }
            then("유스케이스는 getContainerLogService를 실행해야함") {
                verify { getContainerLogService.getLogs(application) }
            }
        }
    }
})