package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.dto.extension.toDto
import com.dcd.server.core.domain.user.model.User
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.user.UserGenerator
import util.workspace.WorkspaceGenerator
import java.util.*

class GetWorkspaceUseCaseTest : BehaviorSpec({
    val queryWorkspacePort = mockk<QueryWorkspacePort>()
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val getWorkspaceUseCase = GetWorkspaceUseCase(queryWorkspacePort, queryApplicationPort)

    given("workspaceId, workspace가 주어지고") {
        val workspaceId = UUID.randomUUID().toString()
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(id = workspaceId, user = user)

        `when`("해당 id를 가진 workspace가 있을때") {
            every { queryWorkspacePort.findById(workspaceId) } returns workspace
            every { queryApplicationPort.findAllByWorkspace(workspace) } returns listOf()
            val result = getWorkspaceUseCase.execute(workspaceId)
            then("queryWorkspacePort가 실행되어야함") {
                verify { queryWorkspacePort.findById(workspaceId) }
            }
            then("result는 워크스페이스의 정보를 담고 있어야함") {
                result.id shouldBe workspaceId
                result.title shouldBe workspace.title
                result.description shouldBe workspace.description
                result.owner shouldBe workspace.owner.toDto()
                result.applicationList.isEmpty() shouldBe true
            }
        }

        `when`("해당 id를 가진 workspace가 없을때") {
            every { queryWorkspacePort.findById(workspaceId) } returns null
            then("WorkspaceNotFoundException이 발생해야함") {
                shouldThrow<WorkspaceNotFoundException> {
                    getWorkspaceUseCase.execute(workspaceId)
                    verify { queryWorkspacePort.findById(workspaceId) }
                }
            }
        }
    }
})