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

    given("CreateApplicationReqDto와 유저가 주어지고") {
    beforeSpec {
        val user = queryUserPort.findById("user2")!!
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        commandWorkspacePort.save(workspace)
        targetWorkspaceId = workspace.id
    }

        val request = CreateApplicationReqDto(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testGithub",
            version = "17",
            port = 8080,
            labels = listOf()
        )
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val id = user.id
        `when`("usecase를 실행하면") {
            every { securityService.getCurrentUserId() } returns id
            every { queryApplicationPort.existsByName(request.name) } returns false
            every { queryUserPort.findById(id) } returns user
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            createApplicationUseCase.execute(workspace.id, request)
            then("repository의 save메서드가 실행되어야함") {
                verify { commandApplicationPort.save(any()) }
                coVerify { cloneApplicationByUrlService.cloneByApplication(any() as Application) }
                coVerify { modifyGradleService.modifyGradleByApplication(any() as Application) }
                coVerify { createDockerFileService.createFileToApplication(any() as Application, request.version) }
                coVerify { buildDockerImageService.buildImageByApplication(any() as Application) }
                coVerify { getExternalPortService.getExternalPort(request.port) }
                coVerify { deleteApplicationDirectoryService.deleteApplicationDirectory(any() as Application) }
            }
        }
    }
})