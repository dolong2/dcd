package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.DeleteApplicationDirectoryServiceImpl
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import util.user.UserGenerator
import java.util.*

class DeleteApplicationDirectoryServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val service = DeleteApplicationDirectoryServiceImpl(commandPort, checkExitValuePort)

    given("애플리케이션이 주이지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("buildImageByApplication 메서드를 실행할때") {
            service.deleteApplicationDirectory(application)

            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("rm -rf ${application.name}") }
            }
        }
    }
})