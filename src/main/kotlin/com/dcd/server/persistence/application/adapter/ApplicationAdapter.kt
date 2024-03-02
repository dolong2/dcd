package com.dcd.server.persistence.application.adapter

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.user.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.workspace.adapter.toDomain
import com.dcd.server.persistence.workspace.adapter.toEntity

fun Application.toEntity(): ApplicationJpaEntity =
    ApplicationJpaEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        env = this.env,
        workspace = this.workspace.toEntity(),
        port = this.port,
        externalPort = this.externalPort,
        version = this.version,
        status = this.status
    )

fun ApplicationJpaEntity.toDomain(): Application =
    Application(
        id = this.id,
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        env = this.env,
        workspace = this.workspace.toDomain(),
        port = this.port,
        externalPort = this.externalPort,
        version = this.version,
        status = this.status
    )