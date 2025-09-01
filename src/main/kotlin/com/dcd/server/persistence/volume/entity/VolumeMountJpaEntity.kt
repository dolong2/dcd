package com.dcd.server.persistence.volume.entity

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "volume_mount_entity")
class VolumeMountJpaEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "application_id")
    val application: ApplicationJpaEntity,
    @ManyToOne
    @JoinColumn(name = "volume_id")
    val volume: VolumeJpaEntity,
    val mountPath: String
)