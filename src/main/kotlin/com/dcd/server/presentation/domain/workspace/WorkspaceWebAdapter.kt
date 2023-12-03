package com.dcd.server.presentation.domain.workspace

import com.dcd.server.core.domain.workspace.usecase.CreateWorkspaceUseCase
import com.dcd.server.presentation.domain.workspace.data.exetension.toDto
import com.dcd.server.presentation.domain.workspace.data.request.CreateWorkspaceRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/workspace")
class WorkspaceWebAdapter(
    private val createWorkspaceUseCase: CreateWorkspaceUseCase
) {
    @PostMapping
    fun createWorkspace(@RequestBody createWorkspaceRequest: CreateWorkspaceRequest): ResponseEntity<Void> =
        createWorkspaceUseCase.execute(createWorkspaceRequest.toDto())
            .run { ResponseEntity.ok().build() }
}