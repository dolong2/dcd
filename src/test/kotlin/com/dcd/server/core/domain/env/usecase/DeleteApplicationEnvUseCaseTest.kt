package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.persistence.env.repository.ApplicationEnvRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
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
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val applicationEnvRepository: ApplicationEnvRepository
) : BehaviorSpec({
    val targetApplicationEnvId = UUID.randomUUID()

    beforeContainer {
        val application = queryApplicationPort.findById("2fb0f315-8272-422f-8e9f-c4f765c022b2")!!
        val applicationEnv = ApplicationEnv(
            id = targetApplicationEnvId,
            name = "testEnv",
            description = "testEnvDescription",
            details = listOf(ApplicationEnvDetail(id = UUID.randomUUID(), key = "testKey", value = "testValue")),
            workspace = application.workspace
        )
        commandApplicationEnvPort.save(applicationEnv, application)
    }

    given("삭제할 애플리케이션의 아이디가 주어지고") {
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
})