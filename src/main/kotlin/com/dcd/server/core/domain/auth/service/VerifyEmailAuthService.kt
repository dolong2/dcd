package com.dcd.server.core.domain.auth.service

import com.dcd.server.core.domain.auth.dto.request.CertificateMailReqDto

interface VerifyEmailAuthService {
    fun verifyCode(certificateEmailReqDto: CertificateMailReqDto)
}