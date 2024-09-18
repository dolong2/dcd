package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.event.ChangeApplicationStatusEvent
import com.dcd.server.core.domain.application.exception.ContainerNotCreatedException
import com.dcd.server.core.domain.application.service.impl.CreateContainerServiceImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher
import util.application.ApplicationGenerator

class CreateContainerServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    val createContainerService = CreateContainerServiceImpl(commandPort, eventPublisher)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("service를 실행할때") {
            createContainerService.createContainer(application, application.externalPort)

            then("컨테이너를 실행하는 명령을 실행해야함") {
                verify {
                    commandPort.executeShellCommand(
                        "docker create --network ${application.workspace.title.replace(' ', '_')} " +
                        "--name ${application.name.lowercase()} " +
                        "-p ${application.externalPort}:${application.port} ${application.name.lowercase()}:latest"
                    )
                }
            }
        }

        `when`("만약 명령이 성공하지 못할때") {
            every {
                commandPort.executeShellCommand(
                    "docker create --network ${application.workspace.title.replace(' ', '_')} " +
                            "--name ${application.name.lowercase()} " +
                            "-p ${application.externalPort}:${application.port} ${application.name.lowercase()}:latest"
                )
            } returns 125
            createContainerService.createContainer(application, application.externalPort)

            then("ContainerNotCreatedException이 발생해야함") {
                verify { eventPublisher.publishEvent(any() as ChangeApplicationStatusEvent) }
            }
        }
    }
})