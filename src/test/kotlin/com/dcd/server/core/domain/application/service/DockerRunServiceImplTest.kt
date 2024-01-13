package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.DockerRunServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.lang.StringBuilder
import java.util.*

class DockerRunServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxUnitFun = true)
    val existsPortService = mockk<ExistsPortService>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val service = DockerRunServiceImpl(queryApplicationPort, existsPortService, commandPort)

    val user =
        User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
    val workspace = Workspace(
        UUID.randomUUID().toString(),
        title = "test workspace",
        description = "test workspace description",
        owner = user
    )
    given("애플리케이션id가 주어지고") {
        val appId = UUID.randomUUID().toString()

        `when`("buildImageByApplicationId를 실행할때") {
            val application = Application(appId, "testName", null, ApplicationType.SPRING_BOOT, "testUrl", mapOf(), workspace, port = 8080)
            every { queryApplicationPort.findById(appId) } returns application
            every { existsPortService.existsPort(application.port) } returns false

            service.runApplication(appId)
            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify { commandPort.executeShellCommand("cd ${application.name} && docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} ${application.name.lowercase()}") }
            }
        }
    }

    given("애플리케이션이 주이지고") {
        val application = Application(UUID.randomUUID().toString(), "testName", null, ApplicationType.SPRING_BOOT, "testUrl", mapOf(), workspace, port = 8080)

        `when`("buildImageByApplication 메서드를 실행할때") {
            every { existsPortService.existsPort(application.port) } returns false
            service.runApplication(application)

            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify { commandPort.executeShellCommand("cd ${application.name} && docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} ${application.name.lowercase()}") }
            }
        }
    }

    given("mysql 애플리케이션이 주어지고") {
        val application = Application(
            UUID.randomUUID().toString(),
            "mysqlTest",
            null,
            ApplicationType.MYSQL,
            "testUrl",
            mapOf(),
            workspace,
            port = 3306
        )

        `when`("buildImageByApplication 메서드를 실행할때") {
            every { existsPortService.existsPort(application.port) } returns false
            service.runApplication(application)

            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify {
                    commandPort.executeShellCommand("docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} mysql")
                }
            }
        }
    }
})