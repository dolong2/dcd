package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import java.util.*

class CreateApplicationUseCaseTest : BehaviorSpec({
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val queryUserPort = mockk<QueryUserPort>()
    val securityService = mockk<SecurityService>()
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val cloneApplicationByUrlService = mockk<CloneApplicationByUrlService>(relaxUnitFun = true)
    val modifyGradleService = mockk<ModifyGradleService>(relaxUnitFun = true)
    val createDockerFileService = mockk<CreateDockerFileService>(relaxUnitFun = true)
    val getExternalPortService = mockk<GetExternalPortService>(relaxed = true)
    val buildDockerImageService = mockk<BuildDockerImageService>(relaxUnitFun = true)
    val createContainerService = mockk<CreateContainerService>(relaxUnitFun = true)
    val deleteApplicationDirectoryService = mockk<DeleteApplicationDirectoryService>(relaxUnitFun = true)
    val createApplicationUseCase = CreateApplicationUseCase(
        commandApplicationPort,
        queryWorkspacePort,
        cloneApplicationByUrlService,
        modifyGradleService,
        createDockerFileService,
        getExternalPortService,
        buildDockerImageService,
        createContainerService,
        deleteApplicationDirectoryService
    )

    given("CreateApplicationReqDto와 유저가 주어지고") {
        val request = CreateApplicationReqDto(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testGithub",
            version = "17",
            port = 8080
        )
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val id = user.id
        `when`("usecase를 실행하면") {
            every { securityService.getCurrentUserId() } returns id
            every { queryUserPort.findById(id) } returns user
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            createApplicationUseCase.execute(workspace.id, request)
            then("repository의 save메서드가 실행되어야함") {
                verify { commandApplicationPort.save(any()) }
                verify { cloneApplicationByUrlService.cloneByApplication(any() as Application) }
                verify { modifyGradleService.modifyGradleByApplication(any() as Application) }
                verify { createDockerFileService.createFileToApplication(any() as Application, request.version) }
                verify { buildDockerImageService.buildImageByApplication(any() as Application) }
                verify { getExternalPortService.getExternalPort(request.port) }
                verify { deleteApplicationDirectoryService.deleteApplicationDirectory(any() as Application) }
            }
        }
    }
})