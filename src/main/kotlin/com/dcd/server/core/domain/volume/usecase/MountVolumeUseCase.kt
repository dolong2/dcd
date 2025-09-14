package com.dcd.server.core.domain.volume.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.event.DeployApplicationEvent
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.volume.dto.request.MountVolumeReqDto
import com.dcd.server.core.domain.volume.exception.AlreadyExistsVolumeMountException
import com.dcd.server.core.domain.volume.exception.VolumeNotFoundException
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.volume.spi.CommandVolumePort
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import org.springframework.context.ApplicationEventPublisher
import java.util.UUID

@UseCase
class MountVolumeUseCase(
    private val queryVolumePort: QueryVolumePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val commandVolumePort: CommandVolumePort,
    private val workspaceInfo: WorkspaceInfo,
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun execute(volumeId: UUID, applicationId: String, mountVolumeReqDto: MountVolumeReqDto) {
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

        if (queryVolumePort.findMountByApplicationAndVolume(application, volume) != null)
            throw AlreadyExistsVolumeMountException()

        val volumeMount = VolumeMount(
            application = application,
            volume = volume,
            mountPath = mountVolumeReqDto.mountPath,
            readOnly = mountVolumeReqDto.readOnly
        )
        commandVolumePort.saveMount(volumeMount)

        //마운트 생성후 대상 애플리케이션 재배포
        eventPublisher.publishEvent(DeployApplicationEvent(listOf(application.id)))
    }
}