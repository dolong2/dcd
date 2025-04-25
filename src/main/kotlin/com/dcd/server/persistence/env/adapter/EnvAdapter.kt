package com.dcd.server.persistence.env.adapter

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.application.adapter.toEntity
import com.dcd.server.persistence.env.entity.ApplicationEnvEntity
import com.dcd.server.persistence.env.entity.GlobalEnvEntity
import com.dcd.server.persistence.workspace.adapter.toEntity

fun ApplicationEnv.toEntity(application: Application): ApplicationEnvEntity =
    ApplicationEnvEntity(
        id = this.id,
        key = this.key,
        value = this.value,
        encryption = this.encryption,
        application = application.toEntity()
    )

fun ApplicationEnvEntity.toDomain(): ApplicationEnv =
    ApplicationEnv(
        id = this.id,
        key = this.key,
        value = this.value,
        encryption = this.encryption
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