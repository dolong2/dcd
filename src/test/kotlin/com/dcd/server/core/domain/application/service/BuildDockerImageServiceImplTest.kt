package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.impl.BuildDockerImageServiceImpl
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import java.util.*

class BuildDockerImageServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val service = BuildDockerImageServiceImpl(commandPort, queryApplicationPort, checkExitValuePort)

    val user = UserGenerator.generateUser()
    given("애플리케이션id가 주어지고") {
        val appId = UUID.randomUUID().toString()

        `when`("buildImageByApplicationId를 실행할때") {
            val workspace = WorkspaceGenerator.generateWorkspace(user = user)
            val application = ApplicationGenerator.generateApplication(id = appId, workspace = workspace)
            every { queryApplicationPort.findById(appId) } returns application

            service.buildImageByApplicationId(appId)
            then("commandPort가 실행되어야함") {
                coVerify { commandPort.executeShellCommand("cd ./${application.name} && ./gradlew clean build") }
                coVerify { commandPort.executeShellCommand("cd ./${application.name} && docker build -t ${application.name.lowercase()}:latest .") }
            }
        }
    }

    given("애플리케이션이 주이지고") {
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)

        `when`("buildImageByApplication 메서드를 실행할때") {
            service.buildImageByApplication(application)

            then("commandPort가 실행되어야함") {
                coVerify { commandPort.executeShellCommand("cd ./${application.name} && ./gradlew clean build") }
                coVerify { commandPort.executeShellCommand("cd ./${application.name} && docker build -t ${application.name.lowercase()}:latest .") }
            }
        }
    }
})