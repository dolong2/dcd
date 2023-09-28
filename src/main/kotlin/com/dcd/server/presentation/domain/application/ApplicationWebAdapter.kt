package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.usecase.CreateApplicationUseCase
import com.dcd.server.core.domain.application.usecase.GetAllApplicationUseCase
import com.dcd.server.core.domain.application.usecase.GetOneApplicationUseCase
import com.dcd.server.core.domain.application.usecase.SpringApplicationRunUseCase
import com.dcd.server.presentation.domain.application.data.exetension.toDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.SpringApplicationRunRequest
import com.dcd.server.presentation.domain.application.data.response.ApplicationListResponse
import com.dcd.server.presentation.domain.application.data.response.ApplicationResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/application")
class ApplicationWebAdapter(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val springApplicationRunUseCase: SpringApplicationRunUseCase,
    private val getAllApplicationUseCase: GetAllApplicationUseCase,
    private val getOneApplicationUseCase: GetOneApplicationUseCase
) {
    @PostMapping
    fun createApplication(@Validated @RequestBody createApplicationRequest: CreateApplicationRequest): ResponseEntity<Void> =
        createApplicationUseCase.execute(createApplicationRequest.toDto())
            .run { ResponseEntity(HttpStatus.CREATED) }

    @PostMapping("/{id}/run/spring")
    fun runApplication(@PathVariable id: String, @RequestBody runApplicationRequest: SpringApplicationRunRequest): ResponseEntity<Void> =
        springApplicationRunUseCase.execute(id, runApplicationRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping
    fun getAllApplication(): ResponseEntity<ApplicationListResponse> =
        getAllApplicationUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{id}")
    fun getOneApplication(@PathVariable id: String): ResponseEntity<ApplicationResponse> =
        getOneApplicationUseCase.execute(id)
            .let { ResponseEntity.ok(it.toResponse()) }
}