package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.workspace.usecase.*
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.common.data.extension.toResponse
import com.dcd.server.presentation.common.data.response.ListResponse
import com.dcd.server.presentation.domain.workspace.data.exetension.toDto
import com.dcd.server.presentation.domain.workspace.data.exetension.toResponse
import com.dcd.server.presentation.domain.workspace.data.request.PutGlobalEnvRequest
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.request.UpdateWorkspaceRequest
import com.dcd.server.presentation.domain.workspace.data.response.CreateWorkspaceResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceResponse
import com.dcd.server.presentation.domain.workspace.data.response.WorkspaceSimpleResponse
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
    private val putGlobalEnvUseCase: PutGlobalEnvUseCase,
    private val deleteGlobalEnvUseCase: DeleteGlobalEnvUseCase
) {
    @PostMapping
    fun createWorkspace(
        @Validated
        @RequestBody createWorkspaceRequest: CreateWorkspaceRequest
    ): ResponseEntity<CreateWorkspaceResponse> =
        createWorkspaceUseCase.execute(createWorkspaceRequest.toDto())
            .run { ResponseEntity(this.toResponse(), HttpStatus.CREATED) }

    @GetMapping
    fun getAllWorkspace(): ResponseEntity<ListResponse<WorkspaceSimpleResponse>> =
        getAllWorkspaceUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse { resDto -> resDto.toResponse() }) }

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

    @PutMapping("/{workspaceId}/env")
    fun putGlobalEnv(@PathVariable workspaceId: String, @RequestBody putGlobalEnvRequest: PutGlobalEnvRequest): ResponseEntity<Void> =
        putGlobalEnvUseCase.execute(workspaceId, putGlobalEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{workspaceId}/env")
    fun deleteGlobalEnv(@PathVariable workspaceId: String, @RequestParam key: String): ResponseEntity<Void> =
        deleteGlobalEnvUseCase.execute(workspaceId, key)
            .run { ResponseEntity.ok().build() }
}