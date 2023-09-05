package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.SignInRequestDto
import com.dcd.server.presentation.domain.auth.data.request.SignInRequest

fun SignInRequest.toDto(): SignInRequestDto =
    SignInRequestDto(
        email = this.email,
        password = this.password
    )