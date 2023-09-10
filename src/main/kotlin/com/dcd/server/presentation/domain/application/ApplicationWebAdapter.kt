package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.usecase.CreateApplicationUseCase
import com.dcd.server.presentation.domain.application.data.exetension.toDto
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/application")
class ApplicationWebAdapter(
    private val createApplicationUseCase: CreateApplicationUseCase
) {
    @PostMapping
    fun createApplication(@Validated @RequestBody createApplicationRequest: CreateApplicationRequest): ResponseEntity<Void> =
        createApplicationUseCase.execute(createApplicationRequest.toDto())
            .run { ResponseEntity(HttpStatus.CREATED) }
}