package com.dcd.server.presentation.domain.volume

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.volume.usecase.CreateVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.DeleteVolumeUseCase
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.domain.volume.data.extension.toDto
import com.dcd.server.presentation.domain.volume.data.request.CreateVolumeRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@WebAdapter("/{workspaceId}/volume")
class VolumeWebAdapter(
    private val createVolumeUseCase: CreateVolumeUseCase,
    private val deleteVolumeUseCase: DeleteVolumeUseCase
) {
    @PostMapping
    @WorkspaceOwnerVerification("#workspaceId")
    fun createVolume(
        @PathVariable workspaceId: String,
        @Validated @RequestBody createVolumeRequest: CreateVolumeRequest
    ): ResponseEntity<Void> =
        createVolumeUseCase.execute(createVolumeRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{volumeId}")
    @WorkspaceOwnerVerification("#workspaceId")
    fun deleteVolume(
        @PathVariable workspaceId: String,
        @PathVariable volumeId: UUID,
    ): ResponseEntity<Void> =
        deleteVolumeUseCase.execute(volumeId)
            .run { ResponseEntity.ok().build() }
}