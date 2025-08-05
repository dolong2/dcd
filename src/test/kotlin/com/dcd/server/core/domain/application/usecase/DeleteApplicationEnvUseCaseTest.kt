package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import util.application.ApplicationGenerator
import util.workspace.WorkspaceGenerator
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeleteApplicationEnvUseCaseTest(
    private val deleteApplicationEnvUseCase: DeleteApplicationEnvUseCase,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort
) : BehaviorSpec({
    val applicationId = "2fb0f315-8272-422f-8e9f-c4f765c022b2"
    val key = "testKey"
    val targetApplicationEnvId = UUID.randomUUID()
    val targetApplicationDetailId = UUID.randomUUID()

    beforeContainer {
        val application = queryApplicationPort.findById(applicationId)!!
        val applicationEnv = ApplicationEnv(
            id = targetApplicationEnvId,
            name = "testEnv",
            description = "testEnvDescription",
            details = listOf(ApplicationEnvDetail(id = targetApplicationDetailId, key = "testKey", value = "testValue")),
            workspace = application.workspace
        )
        commandApplicationEnvPort.save(applicationEnv, application)
    }

    given("애플리케이션 Id와 삭제할 key가 주어지고") {

        `when`("usecase를 실행할때") {
            deleteApplicationEnvUseCase.execute(applicationId, key)

            then("해당 키값을 가진 환경변수가 제거되어야함") {
                val application = queryApplicationPort.findById(applicationId)!!
                queryApplicationEnvPort.findByKeyAndApplication(key, application) shouldBe null
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
    }

    given("존재하지 않는 애플리케이션의 아이디가 주어지고") {
        val notFoundApplicationId = UUID.randomUUID().toString()

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deleteApplicationEnvUseCase.execute(notFoundApplicationId, key)
                }
            }
        }
    }
})