package com.dcd.server.presentation.domain.application

import com.dcd.server.core.common.aop.annotation.WorkspaceOwnerVerification
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
    @WorkspaceOwnerVerification
    fun createApplication(
        @PathVariable workspaceId: String,
        @Validated @RequestBody createApplicationRequest: CreateApplicationRequest
    ): ResponseEntity<Void> =
        createApplicationUseCase.execute(workspaceId, createApplicationRequest.toDto())
            .run { ResponseEntity(HttpStatus.CREATED) }

    @PostMapping("/{id}/run")
    @WorkspaceOwnerVerification
    fun runApplication(@PathVariable workspaceId: String, @PathVariable id: String): ResponseEntity<Void> =
        runApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{id}/deploy")
    @WorkspaceOwnerVerification
    fun deployApplication(@PathVariable workspaceId: String, @PathVariable id: String): ResponseEntity<Void> =
        deployApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @GetMapping
    @WorkspaceOwnerVerification
    fun getAllApplication(@PathVariable workspaceId: String): ResponseEntity<ApplicationListResponse> =
        getAllApplicationUseCase.execute(workspaceId)
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{id}")
    @WorkspaceOwnerVerification
    fun getOneApplication(@PathVariable workspaceId: String, @PathVariable id: String): ResponseEntity<ApplicationResponse> =
        getOneApplicationUseCase.execute(id)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{id}/env")
    @WorkspaceOwnerVerification
    fun addApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable id: String,
        @RequestBody addApplicationEnvRequest: AddApplicationEnvRequest
    ): ResponseEntity<Void> =
        addApplicationEnvUseCase.execute(id, addApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{id}/env")
    @WorkspaceOwnerVerification
    fun deleteApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable id: String,
        @RequestParam key: String
    ): ResponseEntity<Void> =
        deleteApplicationEnvUseCase.execute(id, key)
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{applicationId}/env/{key}")
    @WorkspaceOwnerVerification
    fun updateApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable applicationId: String,
        @PathVariable key: String,
        @RequestBody updateApplicationEnvRequest: UpdateApplicationEnvRequest
    ): ResponseEntity<Void> =
        updateApplicationEnvUseCase.execute(applicationId, key, updateApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{id}/stop")
    @WorkspaceOwnerVerification
    fun stopApplication(@PathVariable workspaceId: String, @PathVariable id: String): ResponseEntity<Void> =
        stopApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{id}")
    @WorkspaceOwnerVerification
    fun deleteApplication(@PathVariable workspaceId: String, @PathVariable id: String): ResponseEntity<Void> =
        deleteApplicationUseCase.execute(id)
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{id}")
    @WorkspaceOwnerVerification
    fun updateApplication(
        @PathVariable workspaceId: String,
        @PathVariable id: String,
        @RequestBody updateApplicationRequest: UpdateApplicationRequest
    ): ResponseEntity<Void> =
        updateApplicationUseCase.execute(id, updateApplicationRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping("/version/{applicationType}")
    @WorkspaceOwnerVerification
    fun getAvailableVersion(@PathVariable workspaceId: String, @PathVariable applicationType: ApplicationType): ResponseEntity<AvailableVersionResponse> =
        getAvailableVersionUseCase.execute(applicationType)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{id}/certificate")
    @WorkspaceOwnerVerification
    fun generateSSLCertificate(
        @PathVariable workspaceId: String,
        @PathVariable id: String,
        @RequestBody generateSSLCertificateRequest: GenerateSSLCertificateRequest
    ): ResponseEntity<Void> =
        generateSSLCertificateUseCase.execute(id, generateSSLCertificateRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping("/{id}/logs")
    @WorkspaceOwnerVerification
    fun getApplicationLog(@PathVariable workspaceId: String, @PathVariable id: String): ResponseEntity<ApplicationLogResponse> =
        getApplicationLogUseCase.execute(id)
            .let { ResponseEntity.ok(it.toResponse()) }
}