package com.dcd.server.presentation.domain.auth

import com.dcd.server.core.domain.auth.usecase.AuthMailSendUseCase
import com.dcd.server.core.domain.auth.usecase.AuthenticateMailUseCase
import com.dcd.server.core.domain.auth.usecase.SignInUseCase
import com.dcd.server.core.domain.auth.usecase.SignUpUseCase
import com.dcd.server.presentation.domain.auth.data.exetension.toDto
import com.dcd.server.presentation.domain.auth.data.exetension.toResponse
import com.dcd.server.presentation.domain.auth.data.request.CertificateMailRequest
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest
import com.dcd.server.presentation.domain.auth.data.request.SignInRequest
import com.dcd.server.presentation.domain.auth.data.request.SignUpRequest
import com.dcd.server.presentation.domain.auth.data.response.SignInResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthWebAdapter(
    private val authMailSendUseCase: AuthMailSendUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val authenticateMailUseCase: AuthenticateMailUseCase,
    private val signInUseCase: SignInUseCase
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
}