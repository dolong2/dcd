package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.impl.CreateContainerServiceImpl
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import com.dcd.server.core.domain.application.util.FailureCase
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import util.application.ApplicationGenerator

class CreateContainerServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val queryVolumePort = mockk<QueryVolumePort>(relaxed = true)
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val createContainerService = CreateContainerServiceImpl(commandPort, queryVolumePort, checkExitValuePort)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("service를 실행할때") {
            createContainerService.createContainer(application, application.externalPort)
            every { queryVolumePort.findAllMountByApplication(application) } returns listOf()

            then("컨테이너를 실행하는 명령을 실행해야함") {
                verify {
                    commandPort.executeShellCommand(
                        "docker create --network ${application.workspace.title.replace(' ', '_')} " +
                        "--name ${application.containerName} " +
                        "-p ${application.externalPort}:${application.port} ${application.containerName}:latest"
                    )
                }
                verify { checkExitValuePort.checkApplicationExitValue(0, application, any() as CoroutineScope, FailureCase.CREATE_CONTAINER_FAILURE) }
                verify { checkExitValuePort.checkApplicationExitValue(0, application, any() as CoroutineScope, FailureCase.CONNECT_NETWORK_FAILURE) }
            }
        }
    }
})