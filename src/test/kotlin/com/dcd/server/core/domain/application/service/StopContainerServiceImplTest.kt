package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.service.impl.StopContainerServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher
import com.dcd.server.infrastructure.test.application.ApplicationGenerator

class StopContainerServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxUnitFun = true)
    val stopContainerService = StopContainerServiceImpl(commandPort, eventPublisher)

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
            stopContainerService.stopContainer(application)

            then("ContainerNotStoppedException이 발생해야함") {
                verify { eventPublisher.publishEvent(any() as ChangeApplicationStatusEvent) }
            }
        }
    }
})