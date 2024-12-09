package com.dcd.server.presentation.domain.auth

import com.dcd.server.core.domain.auth.usecase.*
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.domain.auth.data.exetension.toDto
import com.dcd.server.presentation.domain.auth.data.exetension.toReissueResponse
import com.dcd.server.presentation.domain.auth.data.exetension.toResponse
import com.dcd.server.presentation.domain.auth.data.request.*
import com.dcd.server.presentation.domain.auth.data.response.ReissueTokenResponse
import com.dcd.server.presentation.domain.auth.data.response.SignInResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@WebAdapter("/auth")
class AuthWebAdapter(
    private val authMailSendUseCase: AuthMailSendUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val authenticateMailUseCase: AuthenticateMailUseCase,
    private val signInUseCase: SignInUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val nonAuthChangePasswordUseCase: NonAuthChangePasswordUseCase
) {
    @PostMapping("/email")
    fun sendAuthEmail(
        @Validated
        @RequestBody
        emailSendRequest: EmailSendRequest
    ): ResponseEntity<Void> =
        authMailSendUseCase.execute(emailSendRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/email/certificate")
    fun certificateEmail(
        @Validated
        @RequestBody
        certificateMailRequest: CertificateMailRequest
    ): ResponseEntity<Void> =
        authenticateMailUseCase.execute(certificateMailRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/signup")
    fun signupUser(
        @Validated
        @RequestBody
        signUpRequest: SignUpRequest
    ): ResponseEntity<Void> =
        signUpUseCase.execute(signUpRequest.toDto())
            .run { ResponseEntity(HttpStatus.CREATED) }

    @PostMapping
    fun signIn(
        @Validated
        @RequestBody
        signInRequest: SignInRequest
    ): ResponseEntity<SignInResponse> =
        signInUseCase.execute(signInRequest.toDto())
            .let { ResponseEntity.ok(it.toResponse()) }

    @PatchMapping
    fun reissueToken(@RequestHeader("RefreshToken") refreshToken: String): ResponseEntity<ReissueTokenResponse> =
        reissueTokenUseCase.execute(refreshToken)
            .let { ResponseEntity.ok(it.toReissueResponse()) }

    @DeleteMapping
    fun signOut(): ResponseEntity<Void>  =
        signOutUseCase.execute()
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/password")
    fun changePassword(@RequestBody nonAuthChangePasswordRequest: NonAuthChangePasswordRequest): ResponseEntity<Void> =
        nonAuthChangePasswordUseCase
            .execute(nonAuthChangePasswordRequest.toDto())
            .run { ResponseEntity.ok().build() }
}