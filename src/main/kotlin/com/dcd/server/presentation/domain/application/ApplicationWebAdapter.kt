package com.dcd.server.presentation.domain.application

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
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
    private val getApplicationLogUseCase: GetApplicationLogUseCase,
    private val deployApplicationUseCase: DeployApplicationUseCase,
    private val executeCommandUseCase: ExecuteCommandUseCase,
    private val setApplicationDomainUseCase: SetApplicationDomainUseCase
) {
    @PostMapping
    @WorkspaceOwnerVerification
    fun createApplication(
        @PathVariable workspaceId: String,
        @Validated @RequestBody createApplicationRequest: CreateApplicationRequest
    ): ResponseEntity<CreateApplicationResponse> =
        createApplicationUseCase.execute(workspaceId, createApplicationRequest.toDto())
            .run { ResponseEntity(toResponse(), HttpStatus.CREATED) }

    @PostMapping("/{applicationId}/run")
    @WorkspaceOwnerVerification
    fun runApplication(@PathVariable workspaceId: String, @PathVariable applicationId: String): ResponseEntity<Void> =
        runApplicationUseCase.execute(applicationId)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/run")
    @WorkspaceOwnerVerification
    fun runApplication(@PathVariable workspaceId: String, @RequestParam labels: List<String>): ResponseEntity<Void> =
        runApplicationUseCase.execute(labels)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{applicationId}/deploy")
    @WorkspaceOwnerVerification
    fun deployApplication(@PathVariable workspaceId: String, @PathVariable applicationId: String): ResponseEntity<Void> =
        deployApplicationUseCase.execute(applicationId)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/deploy")
    @WorkspaceOwnerVerification
    fun deployApplicationWithLabels(@PathVariable workspaceId: String, @RequestParam labels: List<String>): ResponseEntity<Void> =
        deployApplicationUseCase.execute(labels)
            .run { ResponseEntity.ok().build() }

    @GetMapping
    @WorkspaceOwnerVerification
    fun getAllApplication(
        @PathVariable workspaceId: String,
        @RequestParam(required = false) labels: List<String>? = null
    ): ResponseEntity<ApplicationListResponse> =
        getAllApplicationUseCase.execute(workspaceId, labels)
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/{applicationId}")
    @WorkspaceOwnerVerification
    fun getOneApplication(@PathVariable workspaceId: String, @PathVariable applicationId: String): ResponseEntity<ApplicationResponse> =
        getOneApplicationUseCase.execute(applicationId)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{applicationId}/env")
    @WorkspaceOwnerVerification
    fun addApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable applicationId: String,
        @RequestBody addApplicationEnvRequest: AddApplicationEnvRequest
    ): ResponseEntity<Void> =
        addApplicationEnvUseCase.execute(applicationId, addApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/env")
    @WorkspaceOwnerVerification
    fun addApplicationEnvWithLabels(
        @PathVariable workspaceId: String,
        @RequestParam labels: List<String>,
        @RequestBody addApplicationEnvRequest: AddApplicationEnvRequest
    ): ResponseEntity<Void> =
        addApplicationEnvUseCase.execute(labels, addApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{applicationId}/env")
    @WorkspaceOwnerVerification
    fun deleteApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable applicationId: String,
        @RequestParam key: String
    ): ResponseEntity<Void> =
        deleteApplicationEnvUseCase.execute(applicationId, key)
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/env")
    @WorkspaceOwnerVerification
    fun deleteApplicationEnvWithLabels(
        @PathVariable workspaceId: String,
        @RequestParam labels: List<String>,
        @RequestParam key: String
    ): ResponseEntity<Void> =
        deleteApplicationEnvUseCase.execute(labels, key)
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/{applicationId}/env")
    @WorkspaceOwnerVerification
    fun updateApplicationEnv(
        @PathVariable workspaceId: String,
        @PathVariable applicationId: String,
        @RequestParam key: String,
        @RequestBody updateApplicationEnvRequest: UpdateApplicationEnvRequest
    ): ResponseEntity<Void> =
        updateApplicationEnvUseCase.execute(applicationId, key, updateApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PatchMapping("/env")
    @WorkspaceOwnerVerification
    fun updateApplicationEnvWithLabels(
        @PathVariable workspaceId: String,
        @RequestParam labels: List<String>,
        @RequestParam key: String,
        @RequestBody updateApplicationEnvRequest: UpdateApplicationEnvRequest
    ): ResponseEntity<Void> =
        updateApplicationEnvUseCase.execute(labels, key, updateApplicationEnvRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{applicationId}/stop")
    @WorkspaceOwnerVerification
    fun stopApplication(@PathVariable workspaceId: String, @PathVariable applicationId: String): ResponseEntity<Void> =
        stopApplicationUseCase.execute(applicationId)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/stop")
    @WorkspaceOwnerVerification
    fun stopApplication(@PathVariable workspaceId: String, @RequestParam labels: List<String>): ResponseEntity<Void> =
        stopApplicationUseCase.execute(labels)
            .run { ResponseEntity.ok().build() }

    @DeleteMapping("/{applicationId}")
    @WorkspaceOwnerVerification
    fun deleteApplication(@PathVariable workspaceId: String, @PathVariable applicationId: String): ResponseEntity<Void> =
        deleteApplicationUseCase.execute(applicationId)
            .run { ResponseEntity.ok().build() }

    @PutMapping("/{applicationId}")
    @WorkspaceOwnerVerification
    fun updateApplication(
        @PathVariable workspaceId: String,
        @PathVariable applicationId: String,
        @RequestBody updateApplicationRequest: UpdateApplicationRequest
    ): ResponseEntity<Void> =
        updateApplicationUseCase.execute(applicationId, updateApplicationRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping("/version/{applicationType}")
    @WorkspaceOwnerVerification
    fun getAvailableVersion(@PathVariable workspaceId: String, @PathVariable applicationType: ApplicationType): ResponseEntity<AvailableVersionResponse> =
        getAvailableVersionUseCase.execute(applicationType)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{applicationId}/domain")
    @WorkspaceOwnerVerification
    fun setApplicationDomain(
        @PathVariable workspaceId: String,
        @PathVariable applicationId: String,
        @RequestBody setDomainRequest: SetDomainRequest
    ): ResponseEntity<Void> =
        setApplicationDomainUseCase.execute(applicationId, setDomainRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @GetMapping("/{applicationId}/logs")
    @WorkspaceOwnerVerification
    fun getApplicationLog(@PathVariable workspaceId: String, @PathVariable applicationId: String): ResponseEntity<ApplicationLogResponse> =
        getApplicationLogUseCase.execute(applicationId)
            .let { ResponseEntity.ok(it.toResponse()) }

    @PostMapping("/{applicationId}/exec")
    @WorkspaceOwnerVerification
    fun execCommand(@PathVariable workspaceId: String, @PathVariable applicationId: String, @Validated @RequestBody executeCommandRequest: ExecuteCommandRequest): ResponseEntity<CommandResultResponse> =
        executeCommandUseCase.execute(applicationId, executeCommandRequest.toDto())
            .let { ResponseEntity.ok(it.toResponse()) }
}