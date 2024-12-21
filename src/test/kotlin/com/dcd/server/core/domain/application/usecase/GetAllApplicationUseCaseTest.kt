package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.dto.extenstion.toDto
import com.dcd.server.core.domain.application.dto.response.ApplicationListResDto
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator

class GetAllApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val getAllApplicationUseCase = GetAllApplicationUseCase(queryApplicationPort, queryWorkspacePort)

    given("applicationList가 주어지고") {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(workspace = workspace)
        val applicationList = listOf(application)
        `when`("usecase를 실행할때") {
            every { queryApplicationPort.findAllByWorkspace(workspace) } returns applicationList
            every { queryWorkspacePort.findById(workspace.id) } returns workspace
            val result = getAllApplicationUseCase.execute(workspace.id, null)
            val target = ApplicationListResDto(applicationList.map { it.toDto() })
            then("result는 target이랑 같아야함") {
                result shouldBe target
            }
        }
    }
})