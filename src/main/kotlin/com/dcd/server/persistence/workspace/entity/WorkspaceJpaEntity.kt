package com.dcd.server.persistence.workspace.entity

import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workspace_entity")
class WorkspaceJpaEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)