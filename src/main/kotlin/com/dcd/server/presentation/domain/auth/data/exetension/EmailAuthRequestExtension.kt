package com.dcd.server.presentation.domain.auth.data.exetension

import com.dcd.server.core.domain.auth.dto.request.CertificateMailRequestDto
import com.dcd.server.core.domain.auth.dto.request.EmailSendRequestDto
import com.dcd.server.presentation.domain.auth.data.request.CertificateMailRequest
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest

fun EmailSendRequest.toDto(): EmailSendRequestDto =
    EmailSendRequestDto(
        email = this.email
    )

fun CertificateMailRequest.toDto(): CertificateMailRequestDto =
    CertificateMailRequestDto(
        email = this.email,
        code = this.code
    )