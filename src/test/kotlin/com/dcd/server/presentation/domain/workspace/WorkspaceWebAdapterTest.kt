package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.common.data.dto.response.ListResDto
import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.workspace.dto.request.PutGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.dto.response.CreateWorkspaceResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceResDto
import com.dcd.server.core.domain.workspace.dto.response.WorkspaceSimpleResDto
import com.dcd.server.core.domain.workspace.usecase.*
import com.dcd.server.presentation.domain.env.data.request.PutEnvRequest
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.request.PutGlobalEnvRequest
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.request.UpdateWorkspaceRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.*

class WorkspaceWebAdapterTest : BehaviorSpec({
    val createWorkspaceUseCase = mockk<CreateWorkspaceUseCase>(relaxUnitFun = true)
    val getAllWorkspaceUseCase = mockk<GetAllWorkspaceUseCase>()
    val getWorkspaceUseCase = mockk<GetWorkspaceUseCase>()
    val deleteWorkspaceUseCase = mockk<DeleteWorkspaceUseCase>(relaxUnitFun = true)
    val updateWorkspaceUseCase = mockk<UpdateWorkspaceUseCase>(relaxUnitFun = true)
    val putGlobalEnvUseCase = mockk<PutGlobalEnvUseCase>(relaxUnitFun = true)
    val deleteGlobalEnvUseCase = mockk<DeleteGlobalEnvUseCase>(relaxUnitFun = true)
    val workspaceWebAdapter = WorkspaceWebAdapter(createWorkspaceUseCase, getAllWorkspaceUseCase, getWorkspaceUseCase, deleteWorkspaceUseCase, updateWorkspaceUseCase, putGlobalEnvUseCase, deleteGlobalEnvUseCase)

    given("CreateWorkspaceRequest가 주어지고") {
        val request = CreateWorkspaceRequest(title = "test", description = "test description")
        `when`("createWorkspace메서드를 실행할때") {
            val testWorkspaceId = "testWorkspaceId"
            every { createWorkspaceUseCase.execute(any() as CreateWorkspaceReqDto) } returns CreateWorkspaceResDto(testWorkspaceId)
            val result = workspaceWebAdapter.createWorkspace(request)
            then("useCase를 실행해야함") {
                verify { createWorkspaceUseCase.execute(any() as CreateWorkspaceReqDto) }
                result.statusCode shouldBe  HttpStatus.CREATED
            }
            then("응답값은 testWorkspaceId를 반환해야함") {
                result.body?.workspaceId shouldBe testWorkspaceId
            }
        }
    }

    given("WorkspaceListResDto가 주어지고") {
        val responseDto = listOf<WorkspaceSimpleResDto>()
        every { getAllWorkspaceUseCase.execute() } returns ListResDto(responseDto)
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
            name = "test",
            status = Status.CREATED
        )
        val workspaceResDto = WorkspaceResDto(
            id = UUID.randomUUID().toString(),
            title = "testTitle",
            description = "testDescription",
            owner = userResponse,
            applicationList = listOf(),
            globalEnv = mapOf()
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

    given("addGlobalEnvRequest가 주어지고") {
        val workspaceId = UUID.randomUUID().toString()
        val request = PutGlobalEnvRequest(
            name = "testName",
            description = "testDescription",
            envList = listOf(PutEnvRequest(key = "testKey", value = "testValue", encryption = false))
        )
        `when`("addGlobalEnv 메서드를 실행할때") {
            val result = workspaceWebAdapter.putGlobalEnv(workspaceId, request)

            then("200 코드가 반환되고, addWorkspaceUseCase를 ") {
                result.statusCode shouldBe HttpStatus.OK
                verify { putGlobalEnvUseCase.execute(workspaceId, any() as PutGlobalEnvReqDto) }
            }
        }
    }

    given("envKey가 주어지고") {
        val workspaceId = UUID.randomUUID().toString()
        val key = "testKey"

        `when`("deleteGlobalEnv 메서드를 실행할때") {
            val result = workspaceWebAdapter.deleteGlobalEnv(workspaceId, key)

            then("200 코드가 반환되고, deleteGlobalEnvUseCase를 실행해야함") {
                result.statusCode shouldBe HttpStatus.OK
                verify { deleteGlobalEnvUseCase.execute(workspaceId, key) }
            }
        }
    }
})