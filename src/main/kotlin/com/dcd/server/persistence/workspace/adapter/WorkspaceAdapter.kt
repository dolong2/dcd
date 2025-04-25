package com.dcd.server.persistence.workspace.adapter

import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.persistence.env.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toDomain
import com.dcd.server.persistence.user.adapter.toEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import java.util.*

fun Workspace.toEntity(): WorkspaceJpaEntity =
    WorkspaceJpaEntity(
        id = UUID.fromString(this.id),
        title = this.title,
        description = this.description,
        globalEnv = listOf(),
        owner = this.owner.toEntity()
    )

fun WorkspaceJpaEntity.toDomain(): Workspace =
    Workspace(
        id = this.id.toString(),
        title = this.title,
        description = this.description,
        globalEnv = this.globalEnv.map { it.toDomain() },
        owner = this.owner.toDomain()
    )