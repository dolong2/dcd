package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.CertificateMailReqDto
import com.dcd.server.core.domain.auth.dto.request.EmailSendReqDto
import com.dcd.server.core.domain.auth.dto.request.SignInReqDto
import com.dcd.server.core.domain.auth.dto.request.SignUpReqDto
import com.dcd.server.presentation.domain.auth.data.request.CertificateMailRequest
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest
import com.dcd.server.presentation.domain.auth.data.request.SignInRequest
import com.dcd.server.presentation.domain.auth.data.request.SignUpRequest

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