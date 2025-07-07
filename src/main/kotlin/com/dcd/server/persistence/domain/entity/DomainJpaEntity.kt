package com.dcd.server.persistence.domain.entity

import com.dcd.server.persistence.application.entity.ApplicationJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name = "domain_entity")
class DomainJpaEntity(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID,
    val name: String,
    val description: String,
    val domain: String,
    @OneToOne
    @JoinColumn(name = "application_id")
    val application: ApplicationJpaEntity
)