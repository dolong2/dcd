package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GetApplicationEnvUseCaseTest(
    private val getApplicationEnvUseCase: GetApplicationEnvUseCase,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    beforeSpec {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        val applicationEnv = ApplicationEnv(
            name = "testEnvName",
            description = "testEnvDescription",
            details = listOf(),
            workspace = workspace
        )
        commandApplicationEnvPort.save(applicationEnv)
    }

    beforeTest {
        val workspace = queryWorkspacePort.findById("d57b42f5-5cc4-440b-8dce-b4fc2e372eff")!!
        workspaceInfo.workspace = workspace
    }

    given("조회할 환경변수가 주어지고") {

        `when`("유스케이스를 실행할때") {
            val result = getApplicationEnvUseCase.execute()

            then("생성된 환경변수의 정보가 담겨있어야함") {
                val list = result.list
                list.size shouldBe 1

                val envFirst = list.first()
                envFirst.name shouldBe "testEnvName"
                envFirst.description shouldBe "testEnvDescription"
            }
        }
    }
})