package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.workspace.dto.request.CreateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.usecase.CreateWorkspaceUseCase
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus

class WorkspaceWebAdapterTest : BehaviorSpec({
    val createWorkspaceUseCase = mockk<CreateWorkspaceUseCase>(relaxUnitFun = true)
    val workspaceWebAdapter = WorkspaceWebAdapter(createWorkspaceUseCase)

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
})