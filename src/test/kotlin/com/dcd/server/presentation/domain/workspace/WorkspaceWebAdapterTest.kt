package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceListResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.usecase.*
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.request.UpdateWorkspaceRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.*

class WorkspaceWebAdapterTest : BehaviorSpec({
    val createWorkspaceUseCase = mockk<CreateWorkspaceUseCase>(relaxUnitFun = true)
    val getAllWorkspaceUseCase = mockk<GetAllWorkspaceUseCase>()
    val getWorkspaceUseCase = mockk<GetWorkspaceUseCase>()
    val deleteWorkspaceUseCase = mockk<DeleteWorkspaceUseCase>(relaxUnitFun = true)
    val updateWorkspaceUseCase = mockk<UpdateWorkspaceUseCase>(relaxUnitFun = true)
    val workspaceWebAdapter = WorkspaceWebAdapter(createWorkspaceUseCase, getAllWorkspaceUseCase, getWorkspaceUseCase, deleteWorkspaceUseCase, updateWorkspaceUseCase)

    given("CreateWorkspaceRequest가 주어지고") {
        val request = CreateWorkspaceRequest(title = "test", description = "test description")
        `when`("createWorkspace메서드를 실행할때") {
            val result = workspaceWebAdapter.createWorkspace(request)
            then("useCase를 실행해야함") {
                verify { createWorkspaceUseCase.execute(any() as CreateWorkspaceReqDto) }
                result.statusCode shouldBe  HttpStatus.OK
            }
        }
    }

    given("WorkspaceListResDto가 주어지고") {
        val responseDto = WorkspaceListResDto(list = listOf())
        every { getAllWorkspaceUseCase.execute() } returns responseDto
        `when`("getAllWorkspace 메서드를 실행할때") {
            val result = workspaceWebAdapter.getAllWorkspace()
            then("usecase를 실행해야함") {
                result.statusCode shouldBe HttpStatus.OK
                verify { getAllWorkspaceUseCase.execute() }
            }
        }
    }

    given("WorkspaceResDto가 주어지고") {
        val userResponse = UserResDto(
            id = UUID.randomUUID().toString(),
            email = "testEmail",
            name = "test"
        )
        val workspaceResDto = WorkspaceResDto(
            id = UUID.randomUUID().toString(),
            title = "testTitle",
            description = "testDescription",
            owner = userResponse,
            applicationList = listOf()
        )
        `when`("getOneWorkspace 메서드를 실행할때") {
            every { getWorkspaceUseCase.execute(workspaceResDto.id) } returns workspaceResDto
            val result = workspaceWebAdapter.getOneWorkspace(workspaceResDto.id)
            then("result의 body는 workspaceResDto의 정보랑 동일해야함") {
                result.statusCode shouldBe HttpStatus.OK
                result.body!! shouldBe workspaceResDto.toResponse()
            }
        }
    }

    given("workspaceId가 주어지고") {
        val workspaceId = UUID.randomUUID().toString()

        `when`("deleteWorkspace 메서드를 실행했을때") {
            val result = workspaceWebAdapter.deleteWorkspace(workspaceId)

            then("200 코드가 반환되고 deleteWorkspaceUseCase를 실행해야함") {
                result.statusCode shouldBe HttpStatus.OK
                verify { deleteWorkspaceUseCase.execute(workspaceId) }
            }
        }

        `when`("updateWorkspace 메서드를 실행했을때") {
            val updatedRequest =
                UpdateWorkspaceRequest(title = "updated title", description = "test description")

            val result = workspaceWebAdapter.updateWorkspace(workspaceId, updatedRequest)

            then("200 코드가 반환되고, updateWorkspaceUseCase를 실행해야함") {
                result.statusCode shouldBe HttpStatus.OK
                verify { updateWorkspaceUseCase.execute(workspaceId, any() as UpdateWorkspaceReqDto) }
            }
        }
    }
})