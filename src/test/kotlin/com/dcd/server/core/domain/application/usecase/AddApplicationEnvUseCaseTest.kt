package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import java.util.*

class AddApplicationEnvUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val addApplicationEnvUseCase = AddApplicationEnvUseCase(queryApplicationPort, commandApplicationPort)

    given("request가 주어지고") {
        val request = AddApplicationEnvReqDto(
            envList = mapOf(Pair("testA", "testB"))
        )
        val application = ApplicationGenerator.generateApplication()
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(application.id) } returns application
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            addApplicationEnvUseCase.execute(application.id, request)
            then("commandApplicationPort의 save메서드로 업데이트 해야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
        `when`("만약 해당 id인 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(application.id) } returns null
            then("ApplicationNotFoundException을 던져야함") {
                shouldThrow<ApplicationNotFoundException> {
                    addApplicationEnvUseCase.execute(application.id, request)
                }
            }
        }
    }
})