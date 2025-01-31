package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.workspace.usecase.*
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.domain.workspace.data.exetension.toDto
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.request.AddGlobalEnvRequest
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.request.UpdateGlobalEnvRequest
import com.dcd.server.presentation.domain.workspace.data.request.UpdateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.response.CreateWorkspaceResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceListResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@WebAdapter("/workspace")
class WorkspaceWebAdapter(
    private val createWorkspaceUseCase: CreateWorkspaceUseCase,
    private val getAllWorkspaceUseCase: GetAllWorkspaceUseCase,
    private val getWorkspaceUseCase: GetWorkspaceUseCase,
    private val deleteWorkspaceUseCase: DeleteWorkspaceUseCase,
    private val updateWorkspaceUseCase: UpdateWorkspaceUseCase,
    private val addGlobalEnvUseCase: AddGlobalEnvUseCase,
    private val deleteGlobalEnvUseCase: DeleteGlobalEnvUseCase,
    private val updateGlobalEnvUseCase: UpdateGlobalEnvUseCase
) {
    @PostMapping
    fun createWorkspace(
        @Validated
        @RequestBody createWorkspaceRequest: CreateWorkspaceRequest
    ): ResponseEntity<CreateWorkspaceResponse> =
        createWorkspaceUseCase.execute(createWorkspaceRequest.toDto())
            .run { ResponseEntity(this.toResponse(), HttpStatus.CREATED) }

    @GetMapping
    fun getAllWorkspace(): ResponseEntity<WorkspaceListResponse> =
        getAllWorkspaceUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{workspaceId}")
    fun getOneWorkspace(@PathVariable workspaceId: String): ResponseEntity<WorkspaceResponse> =
        getWorkspaceUseCase.execute(workspaceId)
            .let { ResponseEntity.ok(it.toResponse()) }

    @DeleteMapping("/{workspaceId}")
    fun deleteWorkspace(@PathVariable workspaceId: String): ResponseEntity<Void> =
        deleteWorkspaceUseCase.execute(workspaceId)
            .let { ResponseEntity.ok().build() }

    @PutMapping("/{workspaceId}")
    fun updateWorkspace(
        @PathVariable
        workspaceId: String,
        @Validated
        @RequestBody
        updateWorkspaceRequest: UpdateWorkspaceRequest
    ): ResponseEntity<Void> =
        updateWorkspaceUseCase.execute(workspaceId, updateWorkspaceRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{workspaceId}/env")
    fun addGlobalEnv(@PathVariable workspaceId: String, @RequestBody addGlobalEnvRequest: AddGlobalEnvRequest): ResponseEntity<Void> =
        addGlobalEnvUseCase.execute(workspaceId, addGlobalEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{workspaceId}/env")
    fun deleteGlobalEnv(@PathVariable workspaceId: String, @RequestParam key: String): ResponseEntity<Void> =
        deleteGlobalEnvUseCase.execute(workspaceId, key)
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{workspaceId}/env")
    fun updateGlobalEnv(@PathVariable workspaceId: String, @RequestParam key: String, @RequestBody updateGlobalEnvRequest: UpdateGlobalEnvRequest): ResponseEntity<Void> =
        updateGlobalEnvUseCase.execute(workspaceId, key, updateGlobalEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }
}