package com.dcd.server.core.domain.volume.dto.extension

import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

fun CreateVolumeReqDto.toEntity(workspace: Workspace): Volume =
    Volume(
        id = UUID.randomUUID(),
        name = this.name,
        description = this.description,
        workspace = workspace,
    )