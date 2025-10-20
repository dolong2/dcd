package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toDetailResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeDetailResDto
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import java.util.UUID

@UseCase(readOnly = true)
class GetOneVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(volumeId: UUID): VolumeDetailResDto {
        val volume = (queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException())

        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())
        if (volume.workspace != workspace)
            throw VolumeNotFoundException()

        val volumeMountList = queryVolumePort.findAllMountByVolume(volume)

        return volume.toDetailResDto(volumeMountList)
    }
}