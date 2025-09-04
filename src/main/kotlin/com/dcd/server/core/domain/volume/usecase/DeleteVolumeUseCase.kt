package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.service.DeleteVolumeService
import com.dcd.server.core.domain.volume.spi.CommandVolumePort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import java.util.UUID

@UseCase
class DeleteVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val commandVolumePort: CommandVolumePort,
    private val deleteVolumeService: DeleteVolumeService
) {
    fun execute(volumeId: UUID) {
        val volume = (queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException())

        val volumeMountList = queryVolumePort.findAllMountByVolume(volume)
        if (volumeMountList.isNotEmpty())
            throw AlreadyExistsVolumeMountException()

        deleteVolumeService.deleteVolume(volume)

        commandVolumePort.delete(volume)
    }
}