package com.dcd.server.presentation.domain.application.data.exetension

import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.request.GenerateSSLCertificateReqDto
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationReqDto
import com.dcd.server.presentation.domain.application.data.request.AddApplicationEnvRequest
import com.dcd.server.presentation.domain.application.data.request.CreateApplicationRequest
import com.dcd.server.presentation.domain.application.data.request.GenerateSSLCertificateRequest
import com.dcd.server.presentation.domain.application.data.request.UpdateApplicationRequest

fun AddApplicationEnvRequest.toDto(): AddApplicationEnvReqDto =
    AddApplicationEnvReqDto(
        envList = this.envList
    )

fun CreateApplicationRequest.toDto(): CreateApplicationReqDto =
    CreateApplicationReqDto(
        name = this.name,
        description = this.description,
        githubUrl = this.githubUrl,
        env = this.env,
        applicationType = this.applicationType,
        port = this.port,
        version = this.version
    )

fun UpdateApplicationRequest.toDto(): UpdateApplicationReqDto =
    UpdateApplicationReqDto(
        name = this.name,
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