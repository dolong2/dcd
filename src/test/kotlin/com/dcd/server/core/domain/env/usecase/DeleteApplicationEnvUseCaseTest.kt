package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.env.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import util.workspace.WorkspaceGenerator
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteApplicationEnvUseCaseTest(
    private val deleteApplicationEnvUseCase: DeleteApplicationEnvUseCase,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryUserPort: QueryUserPort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val targetApplicationEnvId = UUID.randomUUID()

    beforeContainer {
        val application = queryApplicationPort.findById("2fb0f315-8272-422f-8e9f-c4f765c022b2")!!
        val applicationEnv = ApplicationEnv(
            id = targetApplicationEnvId,
            name = "testEnv",
            description = "testEnvDescription",
            details = listOf(ApplicationEnvDetail(id = UUID.randomUUID(), key = "testKey", value = "testValue")),
            workspace = application.workspace,
            labels = listOf(),
        )
        commandApplicationEnvPort.save(applicationEnv, application)
    }

    given("삭제할 애플리케이션의 아이디가 주어지고") {
        beforeTest {
            val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")
            workspaceInfo.workspace = workspace
        }

        `when`("유스케이스를 실행할때") {
            deleteApplicationEnvUseCase.execute(targetApplicationEnvId)

            then("환경변수가 삭제되어야함") {
                queryApplicationEnvPort.findById(targetApplicationEnvId) shouldBe null
            }
        }

        `when`("존재하지 않은 환경변수의 아이디일때") {
            val notfoundEnvId = UUID.randomUUID()

            then("에러가 발생해야함") {
                shouldThrow<ApplicationEnvNotFoundException> {
                    deleteApplicationEnvUseCase.execute(notfoundEnvId)
                }
            }
        }
    }

    given("워크스페이스 정보가 주어지지않고") {
        beforeTest {
            workspaceInfo.workspace = null
        }

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    deleteApplicationEnvUseCase.execute(targetApplicationEnvId)
                }
            }
        }
    }

    given("환경변수가 속해있지 않은 워크스페이스 정보가 주어지고") {
        beforeTest {
            val user = queryUserPort.findById("1e1973eb-3fb9-47ac-9342-c16cd63ffc6f")!!
            val workspace = WorkspaceGenerator.generateWorkspace(user = user)
            commandWorkspacePort.save(workspace)
            workspaceInfo.workspace = workspace
        }

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationEnvNotFoundException> {
                    deleteApplicationEnvUseCase.execute(targetApplicationEnvId)
                }
            }
        }
    }
})