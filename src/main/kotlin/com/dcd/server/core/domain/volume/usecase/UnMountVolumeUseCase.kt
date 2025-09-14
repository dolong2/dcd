package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.event.DeployApplicationEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.exception.VolumeMountNotFoundException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.spi.CommandVolumePort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@UseCase
class UnMountVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandVolumePort: CommandVolumePort,
    private val workspaceInfo: WorkspaceInfo,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun execute(volumeId: UUID, applicationId: String) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val volume = (queryVolumePort.findById(volumeId)
            ?: throw VolumeNotFoundException())
        if (volume.workspace != workspace)
            throw VolumeNotFoundException()

        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())
        if (application.workspace != workspace)
            throw ApplicationNotFoundException()

        val volumeMount = queryVolumePort.findMountByApplicationAndVolume(application, volume)
            ?: throw VolumeMountNotFoundException()
        commandVolumePort.deleteMount(volumeMount)

        eventPublisher.publishEvent(DeployApplicationEvent(listOf(application.id)))
    }
}