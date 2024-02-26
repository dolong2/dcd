package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
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

class DeleteApplicationEnvUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandApplicationPort = mockk<CommandApplicationPort>()
    val validateWorkspaceOwnerService = mockk<ValidateWorkspaceOwnerService>(relaxUnitFun = true)
    val deleteApplicationEnvUseCase = DeleteApplicationEnvUseCase(queryApplicationPort, commandApplicationPort, validateWorkspaceOwnerService)

    given("애플리케이션 Id와 삭제할 key가 주어지고") {
        val applicationId = "testId"
        val key = "testKey"
        val application = ApplicationGenerator.generateApplication(env = mapOf(Pair("testKey", "testValue")))
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findById(applicationId) } returns application
            every { commandApplicationPort.save(any()) } answers { callOriginal() }
            deleteApplicationEnvUseCase.execute(applicationId, key)
            then("commandApplicationPort의 save메서드가 실행되어야함") {
                verify { commandApplicationPort.save(any()) }
            }
        }
        `when`("해당 환경변수가 없을떄") {
            every { queryApplicationPort.findById(applicationId) } returns application
            val notExistsKey = "not exists"
            then("ApplicationEnvNotFoundException이 발생해야함") {
                shouldThrow<ApplicationEnvNotFoundException> {
                    deleteApplicationEnvUseCase.execute(applicationId, notExistsKey)
                }
            }
        }
        `when`("해당 애플리케이션이 없을때") {
            every { queryApplicationPort.findById(applicationId) } returns null
            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deleteApplicationEnvUseCase.execute(applicationId, key)
                }
            }
        }
    }
})