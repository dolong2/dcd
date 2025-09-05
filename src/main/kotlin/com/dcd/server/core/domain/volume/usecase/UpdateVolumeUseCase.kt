package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.volume.dto.extension.toEntity
import com.dcd.server.core.domain.volume.dto.request.UpdateVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.service.CopyVolumeService
import com.dcd.server.core.domain.volume.service.CreateVolumeService
import com.dcd.server.core.domain.volume.service.DeleteVolumeService
import com.dcd.server.core.domain.volume.spi.CommandVolumePort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import java.util.UUID

@UseCase
class UpdateVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val commandVolumePort: CommandVolumePort,
    private val createVolumeService: CreateVolumeService,
    private val copyVolumeService: CopyVolumeService,
    private val deleteVolumeService: DeleteVolumeService,
) {
    fun execute(volumeId: UUID, request: UpdateVolumeReqDto) {
        val volume = (queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException())

        val volumeMountList = queryVolumePort.findAllMountByVolume(volume)
        if (volumeMountList.isNotEmpty())
            throw AlreadyExistsVolumeMountException()

        val newVolume = request.toEntity(volume)
        commandVolumePort.save(newVolume)

        // 수정된 볼륨을 생성후 내용을 복사하고, 기존 볼륨 삭제
        createVolumeService.create(newVolume)
        copyVolumeService.copyVolumeContent(volume, newVolume)
        deleteVolumeService.deleteVolume(volume)
    }
}