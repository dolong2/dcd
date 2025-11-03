package com.dcd.server.persistence.application.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "application_initial_script_entity")
class ApplicationInitialScriptJpaEntity(
    @Id
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    val script: String,
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id")
    val application: ApplicationJpaEntity,
) {
}