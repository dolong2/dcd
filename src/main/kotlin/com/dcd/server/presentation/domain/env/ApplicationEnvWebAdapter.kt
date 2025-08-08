package com.dcd.server.presentation.domain.env

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.env.usecase.DeleteApplicationEnvUseCase
import com.dcd.server.core.domain.env.usecase.PutApplicationEnvUseCase
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.domain.env.data.extension.toDto
import com.dcd.server.presentation.domain.env.data.request.PutApplicationEnvRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@WebAdapter("/{workspaceId}/env")
class ApplicationEnvWebAdapter(
    private val putApplicationEnvUseCase: PutApplicationEnvUseCase,
    private val deleteApplicationEnvUseCase: DeleteApplicationEnvUseCase
) {
    @PostMapping
    @WorkspaceOwnerVerification("#workspaceId")
    fun putApplicationEnv(
        @PathVariable workspaceId: String,
        @RequestBody putApplicationEnvRequest: PutApplicationEnvRequest
    ): ResponseEntity<Void> =
        putApplicationEnvUseCase.execute(putApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{envId}")
    @WorkspaceOwnerVerification("#workspaceId")
    fun deleteApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable envId: UUID
    ): ResponseEntity<Void> =
        deleteApplicationEnvUseCase.execute(envId)
            .run { ResponseEntity.ok().build() }
}