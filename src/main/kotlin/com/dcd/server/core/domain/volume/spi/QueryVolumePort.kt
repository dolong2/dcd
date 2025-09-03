package com.dcd.server.core.domain.volume.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

interface QueryVolumePort {
    fun findById(id: UUID): Volume?

    fun existsVolumeByNameAndWorkspace(name: String, workspace: Workspace): Boolean

    fun findAllMountByApplication(application: Application): List<VolumeMount>

    fun findAllMountByVolume(volume: Volume): List<VolumeMount>
}