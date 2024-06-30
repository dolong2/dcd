package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.*
import com.dcd.server.presentation.domain.application.data.exetension.toDto
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.application.data.request.*
import com.dcd.server.presentation.domain.application.data.response.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/{workspaceId}/application")
class ApplicationWebAdapter(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val runApplicationUseCase: RunApplicationUseCase,
    private val getAllApplicationUseCase: GetAllApplicationUseCase,
    private val getOneApplicationUseCase: GetOneApplicationUseCase,
    private val addApplicationEnvUseCase: AddApplicationEnvUseCase,
    private val deleteApplicationEnvUseCase: DeleteApplicationEnvUseCase,
    private val updateApplicationEnvUseCase: UpdateApplicationEnvUseCase,
    private val stopApplicationUseCase: StopApplicationUseCase,
    private val deleteApplicationUseCase: DeleteApplicationUseCase,
    private val updateApplicationUseCase: UpdateApplicationUseCase,
    private val getAvailableVersionUseCase: GetAvailableVersionUseCase,
    private val generateSSLCertificateUseCase: GenerateSSLCertificateUseCase,
    private val getApplicationLogUseCase: GetApplicationLogUseCase,
    private val deployApplicationUseCase: DeployApplicationUseCase
) {
    @PostMapping
    fun createApplication(@PathVariable workspaceId: String, @Validated @RequestBody createApplicationRequest: CreateApplicationRequest): ResponseEntity<Void> =
        createApplicationUseCase.execute(workspaceId, createApplicationRequest.toDto())
            .run { ResponseEntity(HttpStatus.CREATED) }

    @PostMapping("/{id}/run")
    fun runApplication(@PathVariable id: String, @PathVariable workspaceId: String): ResponseEntity<Void> =
        runApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{id}/deploy")
    fun deployApplication(@PathVariable id: String, @PathVariable workspaceId: String): ResponseEntity<Void> =
        deployApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @GetMapping
    fun getAllApplication(@PathVariable workspaceId: String): ResponseEntity<ApplicationListResponse> =
        getAllApplicationUseCase.execute(workspaceId)
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{id}")
    fun getOneApplication(@PathVariable id: String, @PathVariable workspaceId: String): ResponseEntity<ApplicationResponse> =
        getOneApplicationUseCase.execute(id)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{id}/env")
    fun addApplicationEnv(
        @PathVariable id: String,
        @RequestBody addApplicationEnvRequest: AddApplicationEnvRequest,
        @PathVariable workspaceId: String
    ): ResponseEntity<Void> =
        addApplicationEnvUseCase.execute(id, addApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{id}/env")
    fun deleteApplicationEnv(@PathVariable id: String, @RequestParam key: String, @PathVariable workspaceId: String): ResponseEntity<Void> =
        deleteApplicationEnvUseCase.execute(id, key)
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{applicationId}/env/{key}")
    fun updateApplicationEnv(
        @PathVariable applicationId: String,
        @PathVariable key: String,
        @RequestBody updateApplicationEnvRequest: UpdateApplicationEnvRequest,
        @PathVariable workspaceId: String
    ): ResponseEntity<Void> =
        updateApplicationEnvUseCase.execute(applicationId, key, updateApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{id}/stop")
    fun stopApplication(@PathVariable id: String, @PathVariable workspaceId: String): ResponseEntity<Void> =
        stopApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{id}")
    fun deleteApplication(@PathVariable id: String, @PathVariable workspaceId: String): ResponseEntity<Void> =
        deleteApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{id}")
    fun updateApplication(
        @PathVariable id: String,
        @RequestBody updateApplicationRequest: UpdateApplicationRequest,
        @PathVariable workspaceId: String
    ): ResponseEntity<Void> =
        updateApplicationUseCase.execute(id, updateApplicationRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping("/version/{applicationType}")
    fun getAvailableVersion(@PathVariable applicationType: ApplicationType, @PathVariable workspaceId: String): ResponseEntity<AvailableVersionResponse> =
        getAvailableVersionUseCase.execute(applicationType)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{id}/certificate")
    fun generateSSLCertificate(
        @PathVariable id: String,
        @RequestBody generateSSLCertificateRequest: GenerateSSLCertificateRequest,
        @PathVariable workspaceId: String
    ): ResponseEntity<Void> =
        generateSSLCertificateUseCase.execute(id, generateSSLCertificateRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping("/{id}/logs")
    fun getApplicationLog(@PathVariable id: String, @PathVariable workspaceId: String): ResponseEntity<ApplicationLogResponse> =
        getApplicationLogUseCase.execute(id)
            .let { ResponseEntity.ok(it.toResponse()) }
}