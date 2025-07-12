package com.dcd.server.persistence.workspace.entity

import com.dcd.server.persistence.env.entity.GlobalEnvEntity
import com.dcd.server.persistence.user.entity.UserJpaEntity
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "workspace_entity")
class WorkspaceJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    @OneToMany(mappedBy = "workspace", cascade = [CascadeType.REMOVE])
    val globalEnv: List<GlobalEnvEntity>,
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: UserJpaEntity
)