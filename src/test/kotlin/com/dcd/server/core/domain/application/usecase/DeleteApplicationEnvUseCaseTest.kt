package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

class DeleteApplicationEnvUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val workspaceInfo = WorkspaceInfo()
    val deleteApplicationEnvUseCase = DeleteApplicationEnvUseCase(queryApplicationPort, commandApplicationPort, workspaceInfo)
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteApplicationEnvUseCaseTest(
    private val deleteApplicationEnvUseCase: DeleteApplicationEnvUseCase,
    private val commandApplicationPort: CommandApplicationPort,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) : BehaviorSpec({
    val applicationId = "testId"
    val key = "testKey"

    given("애플리케이션 Id와 삭제할 key가 주어지고") {
        val applicationId = "testId"
        val key = "testKey"
        val application = ApplicationGenerator.generateApplication(env = mapOf(Pair("testKey", "testValue")))
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns application
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            deleteApplicationEnvUseCase.execute(applicationId, key)
            then("commandApplicationPort의 save메서드가 실행되어야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
        `when`("해당 환경변수가 없을떄") {
            every { queryApplicationPort.findById(applicationId) } returns application
            val notExistsKey = "not exists"
            then("ApplicationEnvNotFoundException이 발생해야함") {
                shouldThrow<ApplicationEnvNotFoundException> {
                    deleteApplicationEnvUseCase.execute(applicationId, notExistsKey)
                }
            }
        }
        `when`("해당 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(applicationId) } returns null
            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deleteApplicationEnvUseCase.execute(applicationId, key)
                }
            }
        }
    }
})