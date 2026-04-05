package com.dcd.server.persistence.volume.entity

import com.dcd.server.persistence.workspace.entity.WorkspaceJpaEntity
import com.dcd.server.core.domain.volume.model.enums.VolumeSizeUnit
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
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
    val size: Long? = null,
    @Enumerated(EnumType.STRING)
    val sizeUnit: VolumeSizeUnit? = null, // size가 지정되었는데 null인 경우 기본 단위는 byte로 간주
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workspace_id")
    val workspace: WorkspaceJpaEntity,
)