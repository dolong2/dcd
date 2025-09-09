package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeListResDto
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.spi.QueryVolumePort

@UseCase(readOnly = true)
class GetAllVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(): VolumeListResDto {
        val workspace = (workspaceInfo.workspace
            ?: throw VolumeNotFoundException())

        val volumeList =
            queryVolumePort.findAllVolumeByWorkspace(workspace)
                .map { it.toResDto() }
        return VolumeListResDto(volumeList)
    }
}