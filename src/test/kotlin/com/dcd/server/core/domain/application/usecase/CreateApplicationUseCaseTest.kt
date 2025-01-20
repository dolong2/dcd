package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CreateApplicationUseCaseTest(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val queryUserPort: QueryUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort
) : BehaviorSpec({
    var targetWorkspaceId = ""

    beforeSpec {
        val user = queryUserPort.findById("user2")!!
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        commandWorkspacePort.save(workspace)
        targetWorkspaceId = workspace.id
    }

    given("새로 생성할 애플리케이션 요청이 주어지고") {
        val request = CreateApplicationReqDto(
            name = "testCreateApplication",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testGithub",
            version = "17",
            port = 8080,
            labels = listOf()
        )

        `when`("usecase를 실행하면") {
            createApplicationUseCase.execute(targetWorkspaceId, request)

            then("요청의 이름을 가진 애플리케이션이 존재해야함") {
                queryApplicationPort.existsByName(request.name) shouldBe true
            }
        }
    }
})