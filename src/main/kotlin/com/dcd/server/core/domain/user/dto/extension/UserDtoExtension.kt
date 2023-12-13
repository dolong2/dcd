package com.dcd.server.core.domain.user.dto.extension

import com.dcd.server.core.domain.user.dto.response.UserResDto
import com.dcd.server.core.domain.user.model.User

fun User.toDto(): UserResDto =
    UserResDto(
        id =  this.id,
        email = this.email,
        name = this.name
    )