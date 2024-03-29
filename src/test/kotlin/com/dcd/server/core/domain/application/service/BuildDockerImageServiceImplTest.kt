package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.BuildDockerImageServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import java.util.*

class BuildDockerImageServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val service = BuildDockerImageServiceImpl(commandPort, queryApplicationPort)

    val user = UserGenerator.generateUser()
    given("애플리케이션id가 주어지고") {
        val appId = UUID.randomUUID().toString()

        `when`("buildImageByApplicationId를 실행할때") {
            val workspace = WorkspaceGenerator.generateWorkspace(user = user)
            val application = ApplicationGenerator.generateApplication(id = appId, workspace = workspace)
            every { queryApplicationPort.findById(appId) } returns application

            service.buildImageByApplicationId(appId)
            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("cd ./${application.name} && ./gradlew clean build") }
                verify { commandPort.executeShellCommand("cd ./${application.name} && docker build -t ${application.name.lowercase()}:latest .") }
            }
        }
    }

    given("애플리케이션이 주이지고") {
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)

        `when`("buildImageByApplication 메서드를 실행할때") {
            service.buildImageByApplication(application)

            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("cd ./${application.name} && ./gradlew clean build") }
                verify { commandPort.executeShellCommand("cd ./${application.name} && docker build -t ${application.name.lowercase()}:latest .") }
            }
        }
    }
})