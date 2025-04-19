package com.dcd.server.presentation.domain.application

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.usecase.GetApplicationTypeUseCase
import com.dcd.server.core.domain.application.usecase.GetAvailableVersionUseCase
import com.dcd.server.presentation.common.annotation.WebAdapter
import com.dcd.server.presentation.common.data.extension.toResponse
import com.dcd.server.presentation.common.data.response.ListResponse
import com.dcd.server.presentation.domain.application.data.exetension.toResponse
import com.dcd.server.presentation.domain.application.data.response.AvailableVersionResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@WebAdapter("/application")
class ApplicationStaticWebAdapter(
    private val getAvailableVersionUseCase: GetAvailableVersionUseCase,
    private val getApplicationTypeUseCase: GetApplicationTypeUseCase
) {
    @GetMapping("/{applicationType}/version")
    fun getAvailableVersion(@PathVariable applicationType: ApplicationType): ResponseEntity<AvailableVersionResponse> =
        getAvailableVersionUseCase.execute(applicationType)
            .let { ResponseEntity.ok(it.toResponse()) }

    @GetMapping("/types")
    fun getApplicationTypes(): ResponseEntity<ListResponse<String>> =
        getApplicationTypeUseCase.execute()
            .let { ResponseEntity.ok(it.toResponse { res -> res }) }
}