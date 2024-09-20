package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.impl.ExistsPortServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ExistsPortServiceTest : BehaviorSpec({
    given("service 구현체가 주어지고") {
        val queryApplicationPort = mockk<QueryApplicationPort>()
        val testPort = 9999
        val commandPort = mockk<CommandPort>()
        every { queryApplicationPort.existsByExternalPort(testPort) } returns false
        every { commandPort.executeShellCommandWithResult("lsof -i ${testPort}") } returns emptyList()

        val service = ExistsPortServiceImpl(queryApplicationPort, commandPort)

        `when`("9999 포트가 사용중인지 검증할때") {
            val result = service.existsPort(9999)
            then("결과값은 false여야함") {
                result shouldBe false
                verify { commandPort.executeShellCommandWithResult("lsof -i ${testPort}") }
            }
        }
    }
})