package com.dcd.server.persistence.workspace.entity

import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workspace_entity")
class WorkspaceJpaEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "global_env_entity",
        joinColumns = [JoinColumn(name = "workspace_id", referencedColumnName = "id")])
    @MapKeyColumn(name = "env_key")
    @Column(name = "env_value")
    val globalEnv: Map<String, String>,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)