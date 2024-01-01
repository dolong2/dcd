package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.CertificateMailRequestDto
import com.dcd.server.core.domain.auth.dto.request.EmailSendRequestDto
import com.dcd.server.core.domain.auth.dto.request.SignInRequestDto
import com.dcd.server.core.domain.auth.dto.request.SignUpRequestDto
import com.dcd.server.presentation.domain.auth.data.request.CertificateMailRequest
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest
import com.dcd.server.presentation.domain.auth.data.request.SignInRequest
import com.dcd.server.presentation.domain.auth.data.request.SignUpRequest

fun EmailSendRequest.toDto(): EmailSendRequestDto =
    EmailSendRequestDto(
        email = this.email
    )

fun CertificateMailRequest.toDto(): CertificateMailRequestDto =
    CertificateMailRequestDto(
        email = this.email,
        code = this.code
    )

fun SignInRequest.toDto(): SignInRequestDto =
    SignInRequestDto(
        email = this.email,
        password = this.password
    )

fun SignUpRequest.toDto(): SignUpRequestDto =
    SignUpRequestDto(
        email = this.email,
        password = this.password,
        name = this.name
    )