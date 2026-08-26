package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.*
import com.dcd.server.presentation.domain.application.data.request.*
import com.dcd.server.presentation.domain.env.data.extension.toDto

fun PutApplicationEnvRequest.toDto(): PutApplicationEnvReqDto =
    PutApplicationEnvReqDto(
        name = this.name,
        description = this.description,
        envList = this.envList.map { it.toDto() }
    )

fun CreateApplicationRequest.toDto(): CreateApplicationReqDto =
    CreateApplicationReqDto(
        name = this.name,
        description = this.description,
        gitRepoUrl = this.gitRepoUrl,
        applicationType = this.applicationType,
        port = this.port,
        version = this.version,
        initialScripts = this.initialScripts ?: emptyList(),
        labels = this.labels ?: emptyList()
    )

fun UpdateApplicationRequest.toDto(): UpdateApplicationReqDto =
    UpdateApplicationReqDto(
        name = this.name,
        description = this.description,
        applicationType = this.applicationType,
        gitRepoUrl = this.gitRepoUrl,
        version = this.version,
        port = this.port,
        initialScripts = this.initialScripts ?: emptyList()
    )

fun ExecuteCommandRequest.toDto(): ExecuteCommandReqDto =
    ExecuteCommandReqDto(
        command = this.command
    )

fun SetDomainRequest.toDto(): SetDomainReqDto =
    SetDomainReqDto(
        domain = this.domain
    )