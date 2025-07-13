package com.dcd.server.presentation.domain.domain

import com.dcd.server.core.common.annotation.WorkspaceOwnerVerification
import com.dcd.server.core.domain.domain.usecase.CreateDomainUseCase
import com.dcd.server.core.domain.domain.usecase.DeleteDomainUseCase
import com.dcd.server.presentation.domain.domain.data.dto.toDto
import com.dcd.server.presentation.domain.domain.data.dto.toResponse
import com.dcd.server.presentation.domain.domain.data.request.CreateDomainRequest
import com.dcd.server.presentation.domain.domain.data.response.CreateDomainResponse
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/{workspaceId}/domain")
class DomainWebAdapter(
    private val createDomainUseCase: CreateDomainUseCase,
    private val deleteDomainUseCase: DeleteDomainUseCase
) {
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
}