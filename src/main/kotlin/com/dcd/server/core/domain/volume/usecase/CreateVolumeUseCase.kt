package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.volume.dto.extension.toEntity
import com.dcd.server.core.domain.volume.dto.request.CreateVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeException
import com.dcd.server.core.domain.volume.service.CreateVolumeService
import com.dcd.server.core.domain.volume.spi.CommandVolumePort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class CreateVolumeUseCase(
    private val workspaceInfo: WorkspaceInfo,
    private val queryVolumePort: QueryVolumePort,
    private val commandVolumePort: CommandVolumePort,
    private val createVolumeService: CreateVolumeService
) {
    fun execute(createVolumeReqDto: CreateVolumeReqDto) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val existsVolume = queryVolumePort.existsVolumeByNameAndWorkspace(createVolumeReqDto.name, workspace)
        if (existsVolume)
            throw AlreadyExistsVolumeException()

        val volume = createVolumeReqDto.toEntity(workspace)
        commandVolumePort.save(volume)

        createVolumeService.create(volume)
    }
}