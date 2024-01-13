package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.RunApplicationReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class ApplicationRunUseCaseTest : BehaviorSpec({
    val cloneApplicationByUrlService = mockk<CloneApplicationByUrlService>(relaxUnitFun = true)
    val modifyGradleService = mockk<ModifyGradleService>(relaxUnitFun = true)
    val createDockerFileService = mockk<CreateDockerFileService>(relaxUnitFun = true)
    val buildDockerImageService = mockk<BuildDockerImageService>(relaxUnitFun = true)
    val dockerRunService = mockk<DockerRunService>(relaxUnitFun = true)
    val queryApplicationPort = mockk<QueryApplicationPort>(relaxUnitFun = true)
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val applicationRunUseCase = ApplicationRunUseCase(
        cloneApplicationByUrlService,
        modifyGradleService,
        createDockerFileService,
        buildDockerImageService,
        dockerRunService,
        queryApplicationPort,
        validateWorkspaceOwnerService
    )

    val user =
        User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
    val workspace = Workspace(
        UUID.randomUUID().toString(),
        title = "test workspace",
        description = "test workspace description",
        owner = user
    )
    given("spring boot application, runApplicationDto가 주어지고") {
        val application = Application(
            id = "testId",
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            workspace = workspace,
            port = 8080
        )
        val reqDto = RunApplicationReqDto(17)
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById("testId") } returns application
            applicationRunUseCase.execute("testId", reqDto)
            then("애플리케이션 실행에 관한 service들이 실행되어야함") {
                verify { cloneApplicationByUrlService.cloneByApplication(application) }
                verify { validateWorkspaceOwnerService.validateOwner(workspace) }
                verify { modifyGradleService.modifyGradleByApplication(application) }
                verify { createDockerFileService.createFileToApplication(application, reqDto.langVersion) }
                verify { buildDockerImageService.buildImageByApplication(application) }
                verify { dockerRunService.runApplication(application) }
            }
        }

        `when`("애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById("testId") } returns null
            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    applicationRunUseCase.execute("testId", reqDto)
                }
            }
        }
    }
})