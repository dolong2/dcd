package com.dcd.server.persistence.env.adapter

import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.model.ApplicationEnvMatcher
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.application.adapter.toDomain
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvDetailEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvMatcherEntity
import com.dcd.server.persistence.env.entity.GlobalEnvEntity
import com.dcd.server.persistence.env.entity.common.EnvDetail
import com.dcd.server.persistence.workspace.adapter.toEntity

fun ApplicationEnv.toEntity(): ApplicationEnvEntity =
    ApplicationEnvEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        details = this.details.map { it.toEntity() }
    )

fun ApplicationEnvEntity.toDomain(): ApplicationEnv =
    ApplicationEnv(
        id = this.id,
        name = this.name,
        description = this.description,
        details = this.details.map { it.toDomain() }
    )

fun ApplicationEnvDetailEntity.toDomain(): ApplicationEnvDetail =
    ApplicationEnvDetail(
        id = this.id,
        key = this.envDetail.key,
        value = this.envDetail.value,
        encryption = this.envDetail.encryption,
        applicationEnv = this.applicationEnv.toDomain()
    )

fun ApplicationEnvDetail.toEntity(): ApplicationEnvDetailEntity =
    ApplicationEnvDetailEntity(
        id = this.id,
        envDetail = EnvDetail(this.key, this.value, this.encryption),
        applicationEnv = this.applicationEnv.toEntity()
    )

fun ApplicationEnvMatcher.toEntity(): ApplicationEnvMatcherEntity =
    ApplicationEnvMatcherEntity(
        id = this.id,
        applicationEnv = this.applicationEnv.toEntity(),
        application = this.application.toEntity()
    )

fun ApplicationEnvMatcherEntity.toDomain(): ApplicationEnvMatcher =
    ApplicationEnvMatcher(
        id = this.id,
        applicationEnv = this.applicationEnv.toDomain(),
        application = this.application.toDomain()
    )

fun GlobalEnv.toEntity(workspace: Workspace): GlobalEnvEntity =
    GlobalEnvEntity(
        id = this.id,
        key = this.key,
        value = this.value,
        encryption = this.encryption,
        workspace = workspace.toEntity()
    )

fun GlobalEnvEntity.toDomain(): GlobalEnv =
    GlobalEnv(
        id = this.id,
        key = this.key,
        value = this.value,
        encryption = this.encryption,
    )