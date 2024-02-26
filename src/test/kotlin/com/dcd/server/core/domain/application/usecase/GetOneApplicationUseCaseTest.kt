package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import java.util.*

class GetOneApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val getCurrentUserService = mockk<GetCurrentUserService>()
    val getOneApplicationUseCase = GetOneApplicationUseCase(queryApplicationPort, getCurrentUserService)

    given("애플리케이션이 주어지고") {
        val user = UserGenerator.generateUser()
        val application = ApplicationGenerator.generateApplication(workspace = WorkspaceGenerator.generateWorkspace(user = user))
        `when`("해당 애플리케이션이 있을때") {
            every { queryApplicationPort.findById(application.id) } returns application
            every { getCurrentUserService.getCurrentUser() } returns user
            val result = getOneApplicationUseCase.execute(application.id)
            then("result는 application의 내용이랑 같아야함") {
                result shouldBe application.toDto()
            }
        }
        `when`("해당 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(application.id) } returns null

            then("result는 application의 내용이랑 같아야함") {
                shouldThrow<ApplicationNotFoundException> {
                    getOneApplicationUseCase.execute(application.id)
                }
            }
        }
        `when`("현재 유저가 해당 애플리케이션의 워크스페이스 주인이 아닐때") {
            val another = UserGenerator.generateUser(email = "another")

            every { queryApplicationPort.findById(application.id) } returns application
            every { getCurrentUserService.getCurrentUser() } returns another

            then("WorkspaceOwnerNotSameException이 발생해야함") {
                shouldThrow<WorkspaceOwnerNotSameException> {
                    getOneApplicationUseCase.execute(application.id)
                }
            }
        }
    }
})