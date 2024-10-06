package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.CloneApplicationByUrlServiceImpl
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
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

class CloneApplicationByUrlServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val service = CloneApplicationByUrlServiceImpl(queryApplicationPort, checkExitValuePort, commandPort)

    given("애플리케이션id가 주어지고") {
        val appId = UUID.randomUUID().toString()

        `when`("cloneById 메서드를 실행할때") {
            val workspace = WorkspaceGenerator.generateWorkspace()
            val application = ApplicationGenerator.generateApplication(id = appId, workspace = workspace)
            every { queryApplicationPort.findById(appId) } returns application

            service.cloneById(appId)
            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("git clone ${application.githubUrl} ${application.name}") }
            }
        }
    }

    given("애플리케이션이 주이지고") {
        val workspace = WorkspaceGenerator.generateWorkspace()
        val application = ApplicationGenerator.generateApplication(workspace = workspace)

        `when`("cloneByApplication 메서드를 실행할때") {
            service.cloneByApplication(application)

            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("git clone ${application.githubUrl} ${application.name}") }
            }
        }
    }
})