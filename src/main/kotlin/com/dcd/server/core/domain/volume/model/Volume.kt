package com.dcd.server.core.domain.volume.model

import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

class Volume(
    val id: UUID,
    val name: String,
    val description: String,
    val workspace: Workspace
) {
    val volumeName: String = "${name.replace(" ", "_")}-$id"
}