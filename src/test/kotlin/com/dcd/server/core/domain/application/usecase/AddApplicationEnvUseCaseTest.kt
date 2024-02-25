package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
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

class AddApplicationEnvUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val addApplicationEnvUseCase = AddApplicationEnvUseCase(queryApplicationPort, commandApplicationPort, validateWorkspaceOwnerService)

    given("request가 주어지고") {
        val request = AddApplicationEnvReqDto(
            envList = mapOf(Pair("testA", "testB"))
        )
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val workspace = Workspace(
            UUID.randomUUID().toString(),
            title = "test workspace",
            description = "test workspace description",
            owner = user
        )
        val application = Application(
            id = "testId",
            name = "test",
            description = "test",
            applicationType = ApplicationType.SPRING_BOOT,
            env = mapOf(),
            githubUrl = "testUrl",
            version = "17",
            workspace = workspace,
            port = 8080,
            status = ApplicationStatus.STOPPED
        )
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(application.id) } returns application
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            addApplicationEnvUseCase.execute(application.id, request)
            then("commandApplicationPort의 save메서드로 업데이트 해야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
        `when`("만약 해당 id인 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(application.id) } returns null
            then("ApplicationNotFoundException을 던져야함") {
                shouldThrow<ApplicationNotFoundException> {
                    addApplicationEnvUseCase.execute(application.id, request)
                }
            }
        }
    }
})