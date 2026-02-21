package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.command.dto.CommandResult
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
    val commandPort = mockk<CommandPort>()
    val queryVolumePort = mockk<QueryVolumePort>(relaxed = true)
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val createContainerService = CreateContainerServiceImpl(commandPort, queryVolumePort, checkExitValuePort)

    given("애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication()
        val commandResult = CommandResult(0, emptyList())

        every { queryVolumePort.findAllMountByApplication(application) } returns emptyList()
        every { commandPort.executeShellCommand(any()) } returns commandResult

        `when`("service를 실행하면") {
            createContainerService.createContainer(application, application.externalPort)

            then("docker create 명령이 실행된다") {
                verify(exactly = 1) {
                    commandPort.executeShellCommand(match { it.startsWith("docker create").and(it.contains(application.containerName)) })
                }
            }
        }
    }
})