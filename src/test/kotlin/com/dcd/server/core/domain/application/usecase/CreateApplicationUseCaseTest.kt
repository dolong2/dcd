package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
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
import java.util.*

class CreateApplicationUseCaseTest : BehaviorSpec({
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val queryUserPort = mockk<QueryUserPort>()
    val securityService = mockk<SecurityService>()
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val createApplicationUseCase = CreateApplicationUseCase(commandApplicationPort, queryWorkspacePort, validateWorkspaceOwnerService)

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
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val workspace = Workspace(
            UUID.randomUUID().toString(),
            title = "test workspace",
            description = "test workspace description",
            owner = user
        )
        val id = user.id
        `when`("usecase를 실행하면") {
            every { securityService.getCurrentUserId() } returns id
            every { queryUserPort.findById(id) } returns user
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            createApplicationUseCase.execute(workspace.id, request)
            then("repository의 save메서드가 실행되어야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
    }
})