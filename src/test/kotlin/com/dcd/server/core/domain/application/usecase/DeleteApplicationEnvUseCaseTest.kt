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

    beforeContainer {
        val user = queryUserPort.findById("user2")!!
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        commandWorkspacePort.save(workspace)
        val application = ApplicationGenerator.generateApplication(id = applicationId, env = mapOf(Pair("testKey", "testValue")), workspace = workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 Id와 삭제할 key가 주어지고") {

        `when`("usecase를 실행할때") {
            deleteApplicationEnvUseCase.execute(applicationId, key)

            then("commandApplicationPort의 save메서드가 실행되어야함") {
                val application = queryApplicationPort.findById(applicationId)
                application?.env?.get("testKey") shouldBe null
            }
        }

        `when`("해당 환경변수가 없을떄") {
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