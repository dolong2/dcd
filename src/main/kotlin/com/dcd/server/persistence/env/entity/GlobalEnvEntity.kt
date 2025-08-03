package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.env.entity.common.Env
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
class GlobalEnvEntity(
    id: UUID = UUID.randomUUID(),
    name: String,
    description: String,
    @OneToMany(mappedBy = "globalEnv", cascade = [CascadeType.REMOVE])
    val details: List<GlobalEnvDetailEntity>,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity
) : Env(id, name, description)