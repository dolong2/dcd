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
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

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
    val applicationId = "testId"
    val key = "testKey"

    beforeContainer {
        val user = queryUserPort.findById("user2")!!
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        commandWorkspacePort.save(workspace)
        val application = ApplicationGenerator.generateApplication(id = applicationId, env = mapOf(Pair("testKey", "testValue")), workspace = workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 아이디와 수정할 환경변수값이 주어지고") {
        val applicationId = "testId"
        val envKey = "testKey"
        val request = UpdateApplicationEnvReqDto(newValue = "newValue")

        `when`("해당 애플리케이션이 존재할때") {
            val env = mapOf(Pair(envKey, "testValue"))
            val application = ApplicationGenerator.generateApplication(
                id = applicationId,
                env = env
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            updateApplicationEnvUseCase.execute(applicationId, envKey, request)

            then("애플리케이션의 env를 변경해서 저장해야함") {
                val updatedEnv = env.toMutableMap()
                updatedEnv[envKey] = request.newValue
                val updatedApplication = application.copy(env = updatedEnv)

                updatedApplication.env[envKey] shouldBe request.newValue
                verify { commandApplicationPort.save(updatedApplication) }
            }
        }

        `when`("해당 애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById(applicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    updateApplicationEnvUseCase.execute(applicationId, envKey, request)
                }
            }
        }

        `when`("해당 키를 가지는 환경변수가 존재하지 않을때") {
            val env = mapOf(Pair("NotTestKey", "value"))
            val application = ApplicationGenerator.generateApplication(
                id = applicationId,
                env = env
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            then("ApplicationEnvNotFoundException가 발생해야함") {
                shouldThrow<ApplicationEnvNotFoundException> {
                    updateApplicationEnvUseCase.execute(applicationId, envKey, request)
                }
            }
        }
    }
})