package com.dcd.server.persistence.user.entity

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name = "user_entity")
class UserJpaEntity(
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val password: String,
    val name: String,
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_entity", joinColumns = [JoinColumn(name = "user_id")])
    val roles: MutableList<Role>,
    @Enumerated(EnumType.STRING)
    val status: Status,
)