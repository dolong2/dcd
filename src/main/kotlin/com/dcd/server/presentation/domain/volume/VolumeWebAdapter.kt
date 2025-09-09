package com.dcd.server.presentation.domain.volume

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.volume.usecase.CreateVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.DeleteVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.GetAllVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.GetOneVolumeUseCase
import com.dcd.server.core.domain.volume.usecase.UpdateVolumeUseCase
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.domain.volume.data.extension.toDto
import com.dcd.server.presentation.domain.volume.data.extension.toResponse
import com.dcd.server.presentation.domain.volume.data.request.CreateVolumeRequest
import com.dcd.server.presentation.domain.volume.data.request.UpdateVolumeRequest
import com.dcd.server.presentation.domain.volume.data.response.VolumeDetailResponse
import com.dcd.server.presentation.domain.volume.data.response.VolumeListResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@WebAdapter("/{workspaceId}/volume")
class VolumeWebAdapter(
    private val createVolumeUseCase: CreateVolumeUseCase,
    private val deleteVolumeUseCase: DeleteVolumeUseCase,
    private val updateVolumeUseCase: UpdateVolumeUseCase,
    private val getAllVolumeUseCase: GetAllVolumeUseCase,
    private val getOneVolumeUseCase: GetOneVolumeUseCase
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

    @PutMapping("/{volumeId}")
    @WorkspaceOwnerVerification("#workspaceId")
    fun updateVolume(
        @PathVariable workspaceId: String,
        @PathVariable volumeId: UUID,
        @Validated @RequestBody updateVolumeRequest: UpdateVolumeRequest
    ): ResponseEntity<Void> =
        updateVolumeUseCase.execute(volumeId, updateVolumeRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping
    @WorkspaceOwnerVerification("#workspaceId")
    fun getAllVolume(@PathVariable workspaceId: String): ResponseEntity<VolumeListResponse> =
        getAllVolumeUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{volumeId}")
    @WorkspaceOwnerVerification("#workspaceId")
    fun getVolume(
        @PathVariable workspaceId: String,
        @PathVariable volumeId: UUID
    ): ResponseEntity<VolumeDetailResponse> =
        getOneVolumeUseCase.execute(volumeId)
            .let { ResponseEntity.ok(it.toResponse()) }
}