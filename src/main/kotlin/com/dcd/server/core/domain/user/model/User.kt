package com.dcd.server.core.domain.user.model

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import java.util.*

data class User(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val password: String,
    val name: String,
    val roles: MutableList<Role>,
    val status: Status
) {
    override fun equals(other: Any?): Boolean {
        val user = (other as? User
            ?: return false)
        return this.id == user.id
    }
}
