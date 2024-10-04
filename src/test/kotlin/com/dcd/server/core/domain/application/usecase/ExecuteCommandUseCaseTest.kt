package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.dto.request.ExecuteCommandReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.InvalidApplicationStatusException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.ExecContainerService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.infrastructure.global.jwt.adapter.ParseTokenAdapter
import com.dcd.server.presentation.domain.application.exception.InvalidConnectionInfoException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.security.core.Authentication
import org.springframework.web.socket.WebSocketSession
import util.application.ApplicationGenerator
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator

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

        `when`("execute 메서드를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns givenApplication
            every { commandPort.executeShellCommandWithResult(executedCmd) } returns testCmdResult
            val result = executeCommandUseCase.execute(applicationId, request)

            then("testCmdResult가 응답에 있어야함") {
                result.result shouldBe testCmdResult
                verify { queryApplicationPort.findById(applicationId) }
                verify { commandPort.executeShellCommandWithResult(executedCmd) }
            }
        }
    }

    given("애플리케이션 id, session, cmd가 주어지고") {
        val testApplicationId = "testApplicationId"
        val session = mockk<WebSocketSession>()
        val cmd = "testCmd"

        val givenUser = UserGenerator.generateUser()
        val givenAuthentication = spyk<Authentication>()
        val givenToken = "${givenUser.id}"
        val givenWorkspace = WorkspaceGenerator.generateWorkspace(user = givenUser)
        val givenApplication = ApplicationGenerator.generateApplication(workspace = givenWorkspace, status = ApplicationStatus.RUNNING)

        `when`("세션에 엑세스 토큰이 존재하지 않음") {
            every { session.attributes["accessToken"] } returns null

            then("InvalidConnectionInfoException이 발생해야함") {
                shouldThrow<InvalidConnectionInfoException> {
                    executeCommandUseCase.execute(testApplicationId, session, cmd)
                }
            }
        }

        `when`("주어진 아이디를 가진 애플리케이션이 없을때") {
            every { session.attributes["accessToken"] } returns givenToken
            every { givenAuthentication.name } returns givenUser.id
            every { parseTokenAdapter.getAuthentication(givenToken) } returns givenAuthentication
            every { queryApplicationPort.findById(testApplicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    executeCommandUseCase.execute(testApplicationId, session, cmd)
                }
            }
        }

        `when`("애플리케이션의 상태가 올바르지 않을때") {
            every { queryApplicationPort.findById(testApplicationId) } returns givenApplication.copy(status = ApplicationStatus.STOPPED)

            then("InvalidApplicationStatusException이 발생해야함") {
                shouldThrow<InvalidApplicationStatusException> {
                    executeCommandUseCase.execute(testApplicationId, session, cmd)
                }
            }
        }

        `when`("execute 메서드를 실행할때") {
            every { queryApplicationPort.findById(testApplicationId) } returns givenApplication
            executeCommandUseCase.execute(testApplicationId, session, cmd)

            then("testCmdResult가 응답에 있어야함") {
                verify { queryApplicationPort.findById(testApplicationId) }
                verify { execContainerService.execCmd(givenApplication, session, cmd) }
            }
        }
    }
})