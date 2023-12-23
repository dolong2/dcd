package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.DeleteApplicationDirectoryServiceImpl
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class DeleteApplicationDirectoryServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxUnitFun = true)
    val service = DeleteApplicationDirectoryServiceImpl(commandPort)

    val user =
        User(email = "email", password = "password", name = "testName", roles = mutableListOf(Role.ROLE_USER))
    given("애플리케이션이 주이지고") {
        val workspace = Workspace(
            UUID.randomUUID().toString(),
            title = "test workspace",
            description = "test workspace description",
            owner = user
        )
        val application = Application(UUID.randomUUID().toString(), "testName", null, ApplicationType.SPRING_BOOT, "testUrl", mapOf(), workspace, port = 8080)

        `when`("buildImageByApplication 메서드를 실행할때") {
            service.deleteApplicationDirectory(application)

            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("rm -rf ${application.name}") }
            }
        }
    }
})