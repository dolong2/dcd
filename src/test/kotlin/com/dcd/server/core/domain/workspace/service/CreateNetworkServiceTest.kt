package com.dcd.server.core.domain.workspace.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.workspace.service.impl.CreateNetworkServiceImpl
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.verify
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CreateNetworkServiceTest(
    private val createNetworkService: CreateNetworkServiceImpl,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort
) : BehaviorSpec({

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