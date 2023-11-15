package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.usecase.CreateApplicationUseCase
import com.dcd.server.core.domain.application.usecase.SpringApplicationRunUseCase
import com.dcd.server.presentation.domain.application.data.exetension.toDto
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.RunApplicationRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/application")
class ApplicationWebAdapter(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val springApplicationRunUseCase: SpringApplicationRunUseCase
) {
    @PostMapping("/{workspaceId}")
    fun createApplication(@PathVariable workspaceId: String, @Validated @RequestBody createApplicationRequest: CreateApplicationRequest): ResponseEntity<Void> =
        createApplicationUseCase.execute(workspaceId, createApplicationRequest.toDto())
            .run { ResponseEntity(HttpStatus.CREATED) }

    @PostMapping("/{id}/run")
    fun runApplication(@PathVariable id: String, @RequestBody runApplicationRequest: RunApplicationRequest): ResponseEntity<Void> =
        springApplicationRunUseCase.execute(id, runApplicationRequest.toDto())
            .run { ResponseEntity.ok().build() }

}