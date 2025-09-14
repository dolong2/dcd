package com.dcd.server.persistence.volume.repository

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.volume.entity.VolumeJpaEntity
import com.dcd.server.persistence.volume.entity.VolumeMountJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VolumeMountRepository : JpaRepository<VolumeMountJpaEntity, UUID> {
    fun findAllByVolume(volume: VolumeJpaEntity): List<VolumeMountJpaEntity>
    fun findAllByApplication(application: ApplicationJpaEntity): List<VolumeMountJpaEntity>
    fun findByVolumeAndApplication(volume: VolumeJpaEntity, application: ApplicationJpaEntity): VolumeMountJpaEntity?
}