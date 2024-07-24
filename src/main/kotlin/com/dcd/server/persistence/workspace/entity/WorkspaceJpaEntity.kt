package com.dcd.server.persistence.workspace.entity

import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workspace")
class WorkspaceJpaEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    @ElementCollection
    @CollectionTable(name = "global_env_table",
        joinColumns = [JoinColumn(name = "workspace_id", referencedColumnName = "id")])
    @MapKeyColumn(name = "env_key")
    @Column(name = "env_value")
    val globalEnv: Map<String, String>,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)