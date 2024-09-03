package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.workspace.service.impl.CreateNetworkServiceImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify

class CreateNetworkServiceTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val createNetworkService = CreateNetworkServiceImpl(commandPort)

    given("생성할 네트워크 제목을 주어지고") {
        val testNetworkTitle = "test network"

        `when`("createNetwork 메서드를 실행할때") {
            createNetworkService.createNetwork(testNetworkTitle)

            then("cmd를 실행해야함") {
                val cmd = "docker network create --driver bridge test_network"
                verify { commandPort.executeShellCommand(cmd) }
            }
        }
    }
})