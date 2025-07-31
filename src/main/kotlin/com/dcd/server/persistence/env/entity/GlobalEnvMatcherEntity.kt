package com.dcd.server.persistence.env.entity

import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.*
import java.util.*

@Entity
class GlobalEnvMatcherEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
    @ManyToOne
    @JoinColumn(name = "env_id")
    val globalEnv: GlobalEnvEntity
)