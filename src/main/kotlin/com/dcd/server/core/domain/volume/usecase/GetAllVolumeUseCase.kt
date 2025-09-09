package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toResDto
import com.dcd.server.core.domain.volume.dto.response.VolumeListResDto
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase(readOnly = true)
class GetAllVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(): VolumeListResDto {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val volumeList =
            queryVolumePort.findAllVolumeByWorkspace(workspace)
                .map { it.toResDto() }
        return VolumeListResDto(volumeList)
    }
}