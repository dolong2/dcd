package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.CloneApplicationByUrlServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class GetApplicationByUrlServiceImplTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandPort = mockk<CommandPort>(relaxed = true)
    val serviceImpl = CloneApplicationByUrlServiceImpl(queryApplicationPort, commandPort)

    val id = "testId"
    val testUrl = "testUrl"
    val user =
        User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))

    given("애플리케이션 Id와 url이 주어지고") {
        val application = Application(id, "testName", null, ApplicationType.SPRING_BOOT, testUrl, mapOf(), Workspace(UUID.randomUUID().toString(), title = "test workspace", description = "test workspace description", owner = user), port = 8080)
        every { queryApplicationPort.findById(id) } returns application
        `when`("service를 실행할때") {
            serviceImpl.cloneById(id)
            then("commandPort의 executeShellCommand 메서드를 실행해야함") {
                verify { commandPort.executeShellCommand("git clone $testUrl ${application.name}") }
            }

            every { queryApplicationPort.findById(id) } throws ApplicationNotFoundException()
            then("해당 id를 가진 애플리케이션이 없다면 ApplicationNotFoundException가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    serviceImpl.cloneById(id)
                }
            }
        }
    }

    given("애플리케이션이 주어지고") {
        val application = Application(id, "testName", null, ApplicationType.SPRING_BOOT, testUrl, mapOf(), Workspace(UUID.randomUUID().toString(), title = "test workspace", description = "test workspace description", owner = user), port = 8080)
        `when`("service를 실행할때") {
            serviceImpl.cloneByApplication(application)
            then("commandPort의 executeShellCommand 메서드를 실행해야함") {
                verify { commandPort.executeShellCommand("git clone ${application.githubUrl} ${application.name}") }
            }
        }
    }
})