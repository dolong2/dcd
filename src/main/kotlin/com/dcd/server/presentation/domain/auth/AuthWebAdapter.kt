package com.dcd.server.presentation.domain.auth

import com.dcd.server.core.domain.auth.usecase.AuthMailSendUseCase
import com.dcd.server.presentation.domain.auth.data.exetension.toDto
import com.dcd.server.presentation.domain.auth.data.request.EmailSendRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthWebAdapter(
    private val authMailSendUseCase: AuthMailSendUseCase
) {
    @PostMapping("/email")
    fun sendAuthEmail(
        @Validated
        @RequestBody
        emailSendRequest: EmailSendRequest
    ): ResponseEntity<Void> =
        authMailSendUseCase.execute(emailSendRequest.toDto())
            .run { ResponseEntity.ok().build() }
}