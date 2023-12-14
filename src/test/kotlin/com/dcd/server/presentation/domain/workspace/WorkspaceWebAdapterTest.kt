package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceListResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.usecase.CreateWorkspaceUseCase
import com.dcd.server.core.domain.workspace.usecase.GetAllWorkspaceUseCase
import com.dcd.server.core.domain.workspace.usecase.GetWorkspaceUseCase
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
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
    val workspaceWebAdapter = WorkspaceWebAdapter(createWorkspaceUseCase, getAllWorkspaceUseCase, getWorkspaceUseCase)

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
})