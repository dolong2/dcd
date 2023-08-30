package com.dcd.server.persistence.user.entity

import com.dcd.server.core.domain.auth.model.Role
import jakarta.persistence.*
import java.util.*

@Entity
class UserJpaEntity(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val password: String,
    val name: String,
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Role", joinColumns = [JoinColumn(name = "user_id")])
    val roles: MutableList<Role>
)