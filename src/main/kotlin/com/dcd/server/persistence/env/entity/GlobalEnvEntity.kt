package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.env.entity.common.Env
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class GlobalEnvEntity(
    id: UUID = UUID.randomUUID(),
    key: String,
    value: String,
    encryption: Boolean,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity? = null
) : Env(id, key, value, encryption)