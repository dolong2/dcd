package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import com.dcd.server.persistence.env.repository.ApplicationEnvMatcherRepository
import util.application.ApplicationGenerator
import io.kotest.core.spec.style.BehaviorSpec
import util.workspace.WorkspaceGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.cancel
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CreateApplicationUseCaseTest(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandApplicationPort: CommandApplicationPort,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val applicationEnvMatcherRepository: ApplicationEnvMatcherRepository,
    private val workspaceInfo: WorkspaceInfo
) : BehaviorSpec({
    val targetWorkspaceId = UUID.randomUUID().toString()

    beforeTest {
        val user = queryUserPort.findById("1e1973eb-3fb9-47ac-9342-c16cd63ffc6f")!!
        val workspace = WorkspaceGenerator.generateWorkspace(id = targetWorkspaceId, user = user)
        commandWorkspacePort.save(workspace)
        workspaceInfo.workspace = workspace
    }

    given("새로 생성할 애플리케이션 요청이 주어지고") {
        val request = CreateApplicationReqDto(
            name = "testCreateApplication",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            githubUrl = "testGithub",
            version = "17",
            port = 8080,
            labels = listOf()
        )

        `when`("usecase를 실행하면") {
            createApplicationUseCase.execute(request)
            createApplicationUseCase.coroutineContext.cancel()

            then("요청의 이름을 가진 애플리케이션이 존재해야함") {
                queryApplicationPort.existsByName(request.name) shouldBe true
            }
        }
    }

    given("이미 존재하는 애플리케이션 이름이 주어지고") {
        val targetWorkspace = queryWorkspacePort.findById(targetWorkspaceId)!!
        val generateApplication =
            ApplicationGenerator.generateApplication(name = "testName", workspace = targetWorkspace)
        commandApplicationPort.save(generateApplication)

        val existsApplicationRequest = CreateApplicationReqDto(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            githubUrl = "testGithub",
            version = "17",
            port = 8080,
            labels = listOf()
        )

        `when`("usecase를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<AlreadyExistsApplicationException> {
                    createApplicationUseCase.execute(existsApplicationRequest)
                }
            }
        }
    }

    given("존재하지 않는 워크스페이스 아이디가 주어지고") {
        val notFoundWorkspaceId = UUID.randomUUID().toString()
        val request = CreateApplicationReqDto(
            name = "testCreateApplication",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            githubUrl = "testGithub",
            version = "17",
            port = 8080,
            labels = listOf()
        )

        `when`("usecase를 실행하면") {

            then("에러가 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    workspaceInfo.workspace = queryWorkspacePort.findById(notFoundWorkspaceId)
                    createApplicationUseCase.execute(request)
                }
            }
        }
    }

    given("env와 일치하는 라벨을 가진 애플리케이션 생성 정보가 주어지고") {
        val targetWorkspace = queryWorkspacePort.findById(targetWorkspaceId)!!
        val applicationEnv = ApplicationEnv(
            name = "testEnv",
            description = "testEnv",
            details = listOf(
                ApplicationEnvDetail(
                    id = UUID.randomUUID(),
                    key = "testKey",
                    value = "testValue",
                    encryption = false
                )
            ),
            labels = listOf("testLabel"),
            workspace = targetWorkspace
        )
        commandApplicationEnvPort.save(applicationEnv)
        val request = CreateApplicationReqDto(
            name = "testCreateApplication",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            githubUrl = "testGithub",
            version = "17",
            port = 8080,
            labels = listOf("testLabel")
        )

        `when`("usecase를 실행하면") {
            val result = createApplicationUseCase.execute(request)
            createApplicationUseCase.coroutineContext.cancel()

            then("envMatcher가 생성되어야함") {
                applicationEnvMatcherRepository.flush()

                val application = queryApplicationPort.findById(result.applicationId)!!

                val envMatcherList = applicationEnvMatcherRepository.findAll()
                envMatcherList.size shouldBe 1

                val envMatcher = envMatcherList.first()

                envMatcher.application.id.toString() shouldBe application.id
                envMatcher.applicationEnv.id shouldBe applicationEnv.id
            }
        }
    }
})