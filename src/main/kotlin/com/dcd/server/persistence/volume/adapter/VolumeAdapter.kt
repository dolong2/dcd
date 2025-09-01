package com.dcd.server.persistence.volume.adapter

import com.dcd.server.core.domain.volume.model.Volume
import com.dcd.server.core.domain.volume.model.VolumeMount
import com.dcd.server.persistence.application.adapter.toDomain
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.volume.entity.VolumeJpaEntity
import com.dcd.server.persistence.volume.entity.VolumeMountJpaEntity
import com.dcd.server.persistence.workspace.adapter.toDomain
import com.dcd.server.persistence.workspace.adapter.toEntity

fun Volume.toEntity(): VolumeJpaEntity =
    VolumeJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        physicalPath = this.physicalPath,
        workspace = this.workspace.toEntity()
    )

fun VolumeJpaEntity.toDomain(): Volume =
    Volume(
        id = this.id,
        name = this.name,
        description = this.description,
        physicalPath = this.physicalPath,
        workspace = this.workspace.toDomain()
    )

fun VolumeMount.toEntity(): VolumeMountJpaEntity =
    VolumeMountJpaEntity(
        id = this.id,
        application = this.application.toEntity(),
        volume = this.volume.toEntity(),
        mountPath = this.mountPath,
        readOnly = this.readOnly
    )

fun VolumeMountJpaEntity.toDomain(): VolumeMount =
    VolumeMount(
        id = this.id,
        application = this.application.toDomain(),
        volume = this.volume.toDomain(),
        mountPath = this.mountPath,
        readOnly = this.readOnly
    )