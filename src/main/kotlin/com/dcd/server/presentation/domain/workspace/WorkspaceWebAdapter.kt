package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.workspace.usecase.CreateWorkspaceUseCase
import com.dcd.server.core.domain.workspace.usecase.GetAllWorkspaceUseCase
import com.dcd.server.core.domain.workspace.usecase.GetWorkspaceUseCase
import com.dcd.server.presentation.domain.workspace.data.exetension.toDto
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceListResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/workspace")
class WorkspaceWebAdapter(
    private val createWorkspaceUseCase: CreateWorkspaceUseCase,
    private val getAllWorkspaceUseCase: GetAllWorkspaceUseCase,
    private val getWorkspaceUseCase: GetWorkspaceUseCase
) {
    @PostMapping
    fun createWorkspace(@RequestBody createWorkspaceRequest: CreateWorkspaceRequest): ResponseEntity<Void> =
        createWorkspaceUseCase.execute(createWorkspaceRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping
    fun getAllWorkspace(): ResponseEntity<WorkspaceListResponse> =
        getAllWorkspaceUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{workspaceId}")
    fun getOneWorkspace(@PathVariable workspaceId: String): ResponseEntity<WorkspaceResponse> =
        getWorkspaceUseCase.execute(workspaceId)
            .let { ResponseEntity.ok(it.toResponse()) }
}