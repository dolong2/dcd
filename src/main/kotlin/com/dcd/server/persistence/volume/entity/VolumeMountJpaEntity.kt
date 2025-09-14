package com.dcd.server.persistence.volume.entity

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "volume_mount_entity")
class VolumeMountJpaEntity(
    @MapsId("applicationId")
    @ManyToOne
    @JoinColumn(name = "application_id")
    val application: ApplicationJpaEntity,
    @MapsId("volumeId")
    @ManyToOne
    @JoinColumn(name = "volume_id")
    val volume: VolumeJpaEntity,
    val mountPath: String,
    val readOnly: Boolean
) {
    @EmbeddedId
    val id: VolumeMountId = VolumeMountId(application.id, volume.id)
    @Embeddable
    class VolumeMountId(
        val applicationId: UUID,
        val volumeId: UUID
    ) : Serializable
}