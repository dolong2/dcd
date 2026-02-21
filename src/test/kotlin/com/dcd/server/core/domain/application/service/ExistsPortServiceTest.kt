package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.command.dto.CommandResult
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
        val commandResult = CommandResult(0, emptyList())
        every { queryApplicationPort.existsByExternalPort(testPort) } returns false
        every { commandPort.executeShellCommand("lsof -i :${testPort}") } returns commandResult

        val service = ExistsPortServiceImpl(queryApplicationPort, commandPort)

        `when`("9999 포트가 사용중인지 검증할때") {
            val result = service.existsPort(9999)
            then("결과값은 false여야함") {
                result shouldBe false
                verify(exactly = 1) { commandPort.executeShellCommand("lsof -i :${testPort}") }
            }
        }
    }
})