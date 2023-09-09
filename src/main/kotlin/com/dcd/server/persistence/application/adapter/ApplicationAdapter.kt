package com.dcd.server.persistence.application.adapter

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.user.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toEntity

fun Application.toEntity(): ApplicationJpaEntity =
    ApplicationJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        owner = this.owner.toEntity()
    )

fun ApplicationJpaEntity.toDomain(): Application =
    Application(
        id = this.id,
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        owner = this.owner.toDomain()
    )