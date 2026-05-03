package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.volume.dto.extension.toEntity
import com.dcd.server.core.domain.volume.dto.request.UpdateVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.exception.InvalidVolumeOptionException
import com.dcd.server.core.domain.volume.spi.CommandVolumePort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import java.util.UUID

@UseCase
class UpdateVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val commandVolumePort: CommandVolumePort,
    private val containerPort: ContainerPort,
    private val workspaceInfo: WorkspaceInfo,
) {
    fun execute(volumeId: UUID, request: UpdateVolumeReqDto) {
        val volume = (queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException())

        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        if (workspace != volume.workspace)
            throw VolumeNotFoundException()

        if(request.size == null && request.sizeUnit != null)
            throw InvalidVolumeOptionException()

        val volumeMountList = queryVolumePort.findAllMountByVolume(volume)
        if (volumeMountList.isNotEmpty())
            throw AlreadyExistsVolumeMountException()

        val newVolume = request.toEntity(volume)
        commandVolumePort.save(newVolume)

        // 수정된 볼륨을 생성후 내용을 복사하고, 기존 볼륨 삭제
        containerPort.execute {
            createVolume(newVolume)
            copyVolume(volume, newVolume)
            deleteVolume(volume)
        }
    }
}