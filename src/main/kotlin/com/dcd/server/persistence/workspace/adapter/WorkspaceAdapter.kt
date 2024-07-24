package com.dcd.server.persistence.workspace.adapter

import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.user.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity

fun Workspace.toEntity(): WorkspaceJpaEntity =
    WorkspaceJpaEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        globalEnv = this.globalEnv,
        owner = this.owner.toEntity()
    )

fun WorkspaceJpaEntity.toDomain(): Workspace =
    Workspace(
        id = this.id,
        title = this.title,
        description = this.description,
        globalEnv = this.globalEnv,
        owner = this.owner.toDomain()
    )