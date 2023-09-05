package com.dcd.server.core.domain.auth.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.auth.dto.request.CertificateMailRequestDto
import com.dcd.server.core.domain.auth.service.VerifyEmailAuthService

@UseCase
class AuthenticateMailUseCase(
    private val verifyEmailAuthService: VerifyEmailAuthService
) {
    fun execute(certificateMailRequestDto: CertificateMailRequestDto) {
        verifyEmailAuthService.verifyCode(certificateMailRequestDto.email, certificateMailRequestDto.code)
    }
}