package com.dcd.server.core.domain.volume.model

import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.volume.model.enums.VolumeSizeUnit
import java.util.UUID

class Volume(
    val id: UUID,
    val name: String,
    val description: String,
    val size: Long? = null,
    val sizeUnit: VolumeSizeUnit? = null, // size가 지정되었는데 null인 경우 기본 단위는 byte로 간주
    val workspace: Workspace
) {
    val volumeName: String = "${name.replace(" ", "_")}-$id"

    override fun equals(other: Any?): Boolean {
        if (other !is Volume) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }
}