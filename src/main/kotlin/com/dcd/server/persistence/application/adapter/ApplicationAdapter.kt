package com.dcd.server.persistence.application.adapter

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.ApplicationInitialScript
import com.dcd.server.persistence.application.entity.ApplicationInitialScriptJpaEntity
import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.workspace.adapter.toDomain
import com.dcd.server.persistence.workspace.adapter.toEntity
import java.util.*

fun Application.toEntity(): ApplicationJpaEntity =
    ApplicationJpaEntity(
        id = UUID.fromString(this.id),
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        workspace = this.workspace.toEntity(),
        port = this.port,
        externalPort = this.externalPort,
        version = this.version,
        status = this.status,
        failureReason = this.failureReason,
        labels = this.labels
    )

fun ApplicationJpaEntity.toDomain(): Application =
    Application(
        id = this.id.toString(),
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        workspace = this.workspace.toDomain(),
        port = this.port,
        externalPort = this.externalPort,
        version = this.version,
        status = this.status,
        failureReason = this.failureReason,
        labels = this.labels
    )

fun ApplicationInitialScript.toEntity(): ApplicationInitialScriptJpaEntity =
    ApplicationInitialScriptJpaEntity(
        id = this.id,
        script = this.script,
        application = this.application.toEntity()
    )

fun ApplicationInitialScriptJpaEntity.toDomain(): ApplicationInitialScript =
    ApplicationInitialScript(
        id = this.id,
        script = this.script,
        application = this.application.toDomain()
    )