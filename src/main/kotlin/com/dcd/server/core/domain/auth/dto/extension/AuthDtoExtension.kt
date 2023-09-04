package com.dcd.server.core.domain.auth.dto.extension

import com.dcd.server.core.domain.auth.dto.request.SignUpRequestDto
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.User

fun SignUpRequestDto.toEntity(encodedPassword: String): User =
    User(
        email = this.email,
        password = encodedPassword,
        name = this.name,
        roles = mutableListOf(Role.ROLE_USER)
    )