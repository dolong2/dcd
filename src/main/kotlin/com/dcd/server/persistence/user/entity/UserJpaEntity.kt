package com.dcd.server.persistence.user.entity

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_entity")
class UserJpaEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
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