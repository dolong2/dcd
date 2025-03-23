package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import util.application.ApplicationGenerator
import util.workspace.WorkspaceGenerator
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UpdateApplicationEnvUseCaseTest(
    private val updateApplicationEnvUseCase: UpdateApplicationEnvUseCase,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort
) : BehaviorSpec({
    val applicationId = "2fb0f315-8272-422f-8e9f-c4f765c022b2"
    val key = "testKey"

    beforeContainer {
        val user = queryUserPort.findById("1e1973eb-3fb9-47ac-9342-c16cd63ffc6f")!!
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        commandWorkspacePort.save(workspace)
        val application = ApplicationGenerator.generateApplication(id = applicationId, env = mapOf(Pair("testKey", "testValue")), workspace = workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 아이디와 수정할 환경변수값이 주어지고") {
        val request = UpdateApplicationEnvReqDto(newValue = "newValue")

        `when`("해당 애플리케이션이 존재할때") {

            updateApplicationEnvUseCase.execute(applicationId, key, request)

            then("애플리케이션의 env를 변경해서 저장해야함") {
                val application = queryApplicationPort.findById(applicationId)

                application shouldNotBe null
                application!!.env[key] shouldBe request.newValue
            }
        }
    }

    given("존재하지 않는 애플리케이션 환경변수 키가 주어지고") {
        val notFoundKey = "nfEnvKey"
        val request = UpdateApplicationEnvReqDto(newValue = "newValue")

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationEnvNotFoundException> {
                    updateApplicationEnvUseCase.execute(applicationId, notFoundKey, request)
                }
            }
        }
    }

    given("존재하지 않는 애플리케이션 아이디가 존재하고") {
        val notFoundAppId = UUID.randomUUID().toString()
        val request = UpdateApplicationEnvReqDto(newValue = "newValue")

        `when`("유스케이스를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    updateApplicationEnvUseCase.execute(notFoundAppId, key, request)
                }
            }
        }
    }
})