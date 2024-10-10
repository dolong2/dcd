package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.*
import com.dcd.server.presentation.domain.application.data.request.*

fun AddApplicationEnvRequest.toDto(): AddApplicationEnvReqDto =
    AddApplicationEnvReqDto(
        envList = this.envList
    )

fun CreateApplicationRequest.toDto(): CreateApplicationReqDto =
    CreateApplicationReqDto(
        name = this.name.replace(" ", "-"),
        description = this.description,
        githubUrl = this.githubUrl,
        env = this.env,
        applicationType = this.applicationType,
        port = this.port,
        version = this.version
    )

fun UpdateApplicationRequest.toDto(): UpdateApplicationReqDto =
    UpdateApplicationReqDto(
        name = this.name.replace(" ", "-"),
        description = this.description,
        applicationType = this.applicationType,
        githubUrl = this.githubUrl,
        version = this.version,
        port = this.port
    )

fun GenerateSSLCertificateRequest.toDto(): GenerateSSLCertificateReqDto =
    GenerateSSLCertificateReqDto(
        domain = this.domain
    )

fun UpdateApplicationEnvRequest.toDto(): UpdateApplicationEnvReqDto =
    UpdateApplicationEnvReqDto(
        newValue = this.newValue
    )

fun ExecuteCommandRequest.toDto(): ExecuteCommandReqDto =
    ExecuteCommandReqDto(
        command = this.command
    )