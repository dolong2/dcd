package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.*
import com.dcd.server.presentation.domain.auth.data.request.*

fun EmailSendRequest.toDto(): EmailSendReqDto =
    EmailSendReqDto(
        email = this.email
    )

fun CertificateMailRequest.toDto(): CertificateMailReqDto =
    CertificateMailReqDto(
        email = this.email,
        code = this.code
    )

fun SignInRequest.toDto(): SignInReqDto =
    SignInReqDto(
        email = this.email,
        password = this.password
    )

fun SignUpRequest.toDto(): SignUpReqDto =
    SignUpReqDto(
        email = this.email,
        password = this.password,
        name = this.name
    )

fun NonAuthChangePasswordRequest.toDto(): NonAuthChangePasswordReqDto =
    NonAuthChangePasswordReqDto(
        email = this.email,
        newPassword = this.newPassword
    )