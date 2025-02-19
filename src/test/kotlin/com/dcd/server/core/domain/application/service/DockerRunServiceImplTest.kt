package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.impl.RunContainerServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher
import util.application.ApplicationGenerator
import java.util.*

class DockerRunServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val eventListener = mockk<ApplicationEventPublisher>(relaxed = true)
    val service = RunContainerServiceImpl(queryApplicationPort, commandPort, eventListener)

    given("애플리케이션id가 주어지고") {
        val appId = UUID.randomUUID().toString()

        `when`("executeShellCommand를 실행할때") {
            val application = ApplicationGenerator.generateApplication()
            every { queryApplicationPort.findById(appId) } returns application

            service.runContainer(appId)
            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("docker start ${application.containerName}") }
            }
        }
    }

    given("애플리케이션이 주이지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("executeShellCommand 메서드를 실행할때") {
            service.runContainer(application)

            then("commandPort가 실행되어야함") {
                verify { commandPort.executeShellCommand("docker start ${application.containerName}") }
            }
        }
    }

})