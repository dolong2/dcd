package com.dcd.server.infrastructure.test.user

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.enums.Status
import com.dcd.server.core.domain.user.model.User
import java.util.UUID

object UserGenerator {
    fun generateUser(
        id: String = UUID.randomUUID().toString(),
        email: String = "testEmail",
        password: String = "testPassword",
        name: String = "testName",
        roles: MutableList<Role> = mutableListOf(Role.ROLE_USER),
        status: Status = Status.CREATED
    ): User =
        User(
            id = id,
            email = email,
            password = password,
            name = name,
            roles = roles,
            status = status
        )
}