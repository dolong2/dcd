package com.dcd.server.persistence.workspace.entity

import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.UUID

@Entity
class WorkspaceJpaEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)