package com.dcd.server.persistence.workspace.entity

import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

@Entity
@Table(name = "workspace_entity")
class WorkspaceJpaEntity(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
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