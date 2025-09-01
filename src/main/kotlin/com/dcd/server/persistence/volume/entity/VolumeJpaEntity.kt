package com.dcd.server.persistence.volume.entity

import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "volume_entity")
class VolumeJpaEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    val name: String,
    val description: String,
    val physicalPath: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
)