package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.SignUpRequestDto
import com.dcd.server.presentation.domain.auth.data.request.SignUpRequest

fun SignUpRequest.toDto(): SignUpRequestDto =
    SignUpRequestDto(
        email = this.email,
        password = this.password,
        name = this.name
    )