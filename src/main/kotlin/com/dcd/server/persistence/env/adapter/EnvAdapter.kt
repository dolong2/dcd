package com.dcd.server.persistence.env.adapter

import com.dcd.server.core.domain.env.model.*
import com.dcd.server.persistence.application.adapter.toDomain
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.env.entity.*
import com.dcd.server.persistence.env.entity.common.EnvDetail

fun ApplicationEnv.toEntity(): ApplicationEnvEntity =
    ApplicationEnvEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        details = listOf()
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

fun GlobalEnv.toEntity(): GlobalEnvEntity =
    GlobalEnvEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        details = listOf()
    )

fun GlobalEnvEntity.toDomain(): GlobalEnv =
    GlobalEnv(
        id = this.id,
        name = this.name,
        description = this.description,
        details = this.details.map { it.toDomain() }
    )

fun GlobalEnvDetail.toEntity(): GlobalEnvDetailEntity =
    GlobalEnvDetailEntity(
        id = this.id,
        envDetail = EnvDetail(this.key, this.value, this.encryption),
        globalEnv = this.globalEnv.toEntity(),
    )

fun GlobalEnvDetailEntity.toDomain(): GlobalEnvDetail =
    GlobalEnvDetail(
        id = this.id,
        key = this.envDetail.key,
        value = this.envDetail.value,
        encryption = this.envDetail.encryption,
        globalEnv = this.globalEnv.toDomain()
    )