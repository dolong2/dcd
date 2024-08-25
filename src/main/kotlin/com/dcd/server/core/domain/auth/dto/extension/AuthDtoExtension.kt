package com.dcd.server.core.domain.auth.dto.extension

import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.user.model.Status
import com.dcd.server.core.domain.user.model.User

fun SignUpReqDto.toEntity(encodedPassword: String): User =
    User(
        email = this.email,
        password = encodedPassword,
        name = this.name,
        roles = mutableListOf(Role.ROLE_USER),
        status = Status.PENDING
    )