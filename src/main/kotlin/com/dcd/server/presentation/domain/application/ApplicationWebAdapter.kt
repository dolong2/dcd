package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.usecase.*
import com.dcd.server.presentation.domain.application.data.exetension.toDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.application.data.request.AddApplicationEnvRequest
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
    private val getOneApplicationUseCase: GetOneApplicationUseCase,
    private val addApplicationEnvUseCase: AddApplicationEnvUseCase,
    private val deleteApplicationEnvUseCase: DeleteApplicationEnvUseCase
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

    @PostMapping("/{id}/env")
    fun addApplicationEnv(@PathVariable id: String, @RequestBody addApplicationEnvRequest: AddApplicationEnvRequest): ResponseEntity<Void> =
        addApplicationEnvUseCase.execute(id, addApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{id}/env")
    fun deleteApplicationEnv(@PathVariable id: String, @RequestParam key: String): ResponseEntity<Void> =
        deleteApplicationEnvUseCase.execute(id, key)
            .run { ResponseEntity.ok().build() }
}