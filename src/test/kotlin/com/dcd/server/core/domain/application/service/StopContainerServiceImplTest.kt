package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ContainerNotStoppedException
import com.dcd.server.core.domain.application.service.impl.StopContainerServiceImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator

class StopContainerServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val stopContainerService = StopContainerServiceImpl(commandPort)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("컨테이너 정지 명령이 성공했을때") {
            stopContainerService.stopContainer(application)

            then("컨테이너 정지 명령이 실행되야함") {
                verify { commandPort.executeShellCommand("docker stop ${application.name.lowercase()}") }
            }
        }

        `when`("컨테이너 정지 명령이 실패했을때") {
            every { commandPort.executeShellCommand("docker stop ${application.name.lowercase()}") } returns 125

            then("ContainerNotStoppedException이 발생해야함") {
                shouldThrow<ContainerNotStoppedException> {
                    stopContainerService.stopContainer(application)
                }
            }
        }
    }
})