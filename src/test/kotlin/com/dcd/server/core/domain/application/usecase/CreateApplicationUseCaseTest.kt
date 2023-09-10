package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.auth.exception.UserNotFoundException
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.user.spi.QueryUserPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CreateApplicationUseCaseTest : BehaviorSpec({
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val queryUserPort = mockk<QueryUserPort>()
    val securityService = mockk<SecurityService>()
    val createApplicationUseCase = CreateApplicationUseCase(commandApplicationPort, securityService, queryUserPort)

    given("CreateApplicationReqDto와 유저가 주어지고") {
        val request = CreateApplicationReqDto(
            name = "testName",
            description = "testDescription",
            applicationType = ApplicationType.SPRING_BOOT,
            githubUrl = "testGithub"
        )
        val user =
            User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
        val id = user.id
        `when`("usecase를 실행하면") {
            every { securityService.getCurrentUserId() } returns id
            every { queryUserPort.findById(id) } returns user
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            createApplicationUseCase.execute(request)
            then("repository의 save메서드가 실행되어야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
        `when`("해당 유저가 존재하지 않으면") {
            every { securityService.getCurrentUserId() } returns id
            every { queryUserPort.findById(id) } throws UserNotFoundException()
            then("repository의 save메서드가 실행되어야함") {
                shouldThrow<UserNotFoundException> {
                    createApplicationUseCase.execute(request)
                }
            }
        }
    }
})