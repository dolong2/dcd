package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.dto.request.ExecuteCommandReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.InvalidApplicationStatusException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ExecContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator

class ExecuteCommandUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>(relaxUnitFun = true)
    val parseTokenAdapter = mockk<ParseTokenAdapter>(relaxUnitFun = true)
    val execContainerService = mockk<ExecContainerService>(relaxUnitFun = true)
    val commandPort = mockk<CommandPort>(relaxUnitFun = true)

    val executeCommandUseCase =
        ExecuteCommandUseCase(queryApplicationPort, execContainerService, parseTokenAdapter, commandPort)

    given("애플리케이션 id, ExecuteCommandReqDto가 주어지고") {
        val applicationId = "testApplicationId"
        val cmd = "test"
        val request = ExecuteCommandReqDto(cmd)

        val givenApplication = ApplicationGenerator.generateApplication(id = applicationId, status = ApplicationStatus.RUNNING)
        val executedCmd = "docker exec ${givenApplication.name.lowercase()} $cmd"
        val testCmdResult = listOf("test cmd result")

        `when`("주어진 아이디를 가진 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(applicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    executeCommandUseCase.execute(applicationId, request)
                }
            }
        }

        `when`("애플리케이션의 상태가 올바르지 않을때") {
            every { queryApplicationPort.findById(applicationId) } returns givenApplication.copy(status = ApplicationStatus.STOPPED)

            then("InvalidApplicationStatusException이 발생해야함") {
                shouldThrow<InvalidApplicationStatusException> {
                    executeCommandUseCase.execute(applicationId, request)
                }
            }
        }
    }
})