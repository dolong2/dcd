package com.dcd.server.presentation.domain.domain

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.domain.usecase.*
import com.dcd.server.presentation.common.data.extension.toResponse
import com.dcd.server.presentation.common.data.response.ListResponse
import com.dcd.server.presentation.domain.domain.data.extension.toDto
import com.dcd.server.presentation.domain.domain.data.extension.toResponse
import com.dcd.server.presentation.domain.domain.data.request.ConnectDomainRequest
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest
import com.dcd.server.presentation.domain.domain.data.response.CreateDomainResponse
import com.dcd.server.presentation.domain.domain.data.response.DomainResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/{workspaceId}/domain")
class DomainWebAdapter(
    private val createDomainUseCase: CreateDomainUseCase,
    private val deleteDomainUseCase: DeleteDomainUseCase,
    private val connectDomainUseCase: ConnectDomainUseCase,
    private val disconnectDomainUseCase: DisconnectDomainUseCase,
    private val getDomainUseCase: GetDomainUseCase
) {
    @GetMapping
    @WorkspaceOwnerVerification("#workspaceId")
    fun getDomainList(@PathVariable workspaceId: String): ResponseEntity<ListResponse<DomainResponse>> =
        getDomainUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse { resDto -> resDto.toResponse() }) }

    @PostMapping
    @WorkspaceOwnerVerification("#workspaceId")
    fun createDomain(
        @PathVariable workspaceId: String,
        @Validated @RequestBody createDomainRequest: CreateDomainRequest
    ): ResponseEntity<CreateDomainResponse> =
        createDomainUseCase.execute(createDomainRequest.toDto())
            .let { ResponseEntity.ok(it.toResponse()) }

    @DeleteMapping("/{domainId}")
    @WorkspaceOwnerVerification("#workspaceId")
    fun deleteDomain(
        @PathVariable workspaceId: String,
        @PathVariable domainId: String
    ): ResponseEntity<Void> =
        deleteDomainUseCase.execute(domainId)
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{domainId}/connect")
    @WorkspaceOwnerVerification("#workspaceId")
    fun connectDomain(
        @PathVariable workspaceId: String,
        @PathVariable domainId: String,
        @Validated @RequestBody connectDomainRequest: ConnectDomainRequest
    ): ResponseEntity<Void> =
        connectDomainUseCase.execute(domainId, connectDomainRequest.toDto())
            .run { ResponseEntity.ok().build() }

    @PostMapping("/{domainId}/disconnect")
    @WorkspaceOwnerVerification("#workspaceId")
    fun disconnectDomain(
        @PathVariable workspaceId: String,
        @PathVariable domainId: String
    ): ResponseEntity<Void> =
        disconnectDomainUseCase.execute(domainId)
            .run { ResponseEntity.ok().build() }
}