package com.dcd.server.persistence.volume

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.core.domain.volume.spi.VolumePort
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.volume.adapter.toDomain
import com.dcd.server.persistence.volume.adapter.toEntity
import com.dcd.server.persistence.volume.repository.VolumeMountRepository
import com.dcd.server.persistence.volume.repository.VolumeRepository
import com.dcd.server.persistence.workspace.adapter.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class VolumePersistenceAdapter(
    private val volumeRepository: VolumeRepository,
    private val volumeMountRepository: VolumeMountRepository
) : VolumePort{
    override fun save(volume: Volume) {
        volumeRepository.save(volume.toEntity())
    }

    override fun delete(volume: Volume) {
        volumeRepository.deleteById(volume.id)
    }

    override fun saveMount(volumeMount: VolumeMount) {
        volumeMountRepository.save(volumeMount.toEntity())
    }

    override fun findById(id: UUID): Volume? =
        volumeRepository.findByIdOrNull(id)
            ?.toDomain()

    override fun findAllVolumeByWorkspace(workspace: Workspace): List<Volume> =
        volumeRepository.findAllByWorkspace(workspace.toEntity())
            .map { it.toDomain() }

    override fun existsVolumeByNameAndWorkspace(
        name: String,
        workspace: Workspace,
    ): Boolean =
        volumeRepository.existsByNameAndWorkspace(name, workspace.toEntity())

    override fun findAllMountByApplication(application: Application): List<VolumeMount> =
        volumeMountRepository.findAllByApplication(application.toEntity())
            .map { it.toDomain() }

    override fun findAllMountByVolume(volume: Volume): List<VolumeMount> =
        volumeMountRepository.findAllByVolume(volume.toEntity())
            .map { it.toDomain() }
}