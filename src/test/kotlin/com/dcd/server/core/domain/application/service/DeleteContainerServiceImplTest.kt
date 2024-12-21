package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.impl.DeleteContainerServiceImpl
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify
import com.dcd.server.infrastructure.test.application.ApplicationGenerator

class DeleteContainerServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val service = DeleteContainerServiceImpl(commandPort, checkExitValuePort)

    given("애플리케이션이 주이지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("buildImageByApplication 메서드를 실행할때") {
            service.deleteContainer(application)

            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("docker rm ${application.name.lowercase()}") }
            }
        }
    }
})