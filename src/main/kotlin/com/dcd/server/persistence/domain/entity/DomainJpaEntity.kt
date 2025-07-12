package com.dcd.server.persistence.domain.entity

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "domain_entity")
class DomainJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    val name: String,
    val description: String,
    @OneToOne
    @JoinColumn(name = "application_id", nullable = true)
    val application: ApplicationJpaEntity?,
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
)