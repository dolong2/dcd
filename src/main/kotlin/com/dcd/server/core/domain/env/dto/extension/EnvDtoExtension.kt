package com.dcd.server.core.domain.env.dto.extension

import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.domain.env.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto
import com.dcd.server.core.domain.env.dto.response.ApplicationEnvSimpleResDto
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

fun PutApplicationEnvReqDto.toModel(workspace: Workspace, encryptService: EncryptService): ApplicationEnv =
    ApplicationEnv(
        name = this.name,
        description = this.description,
        details = this.details.map { it.toModel(encryptService) },
        workspace = workspace
    )

fun PutEnvReqDto.toModel(encryptService: EncryptService): ApplicationEnvDetail {
    val envValue =
        if (this.encryption)
            encryptService.encryptData(this.value)
        else
            this.value
    return ApplicationEnvDetail(
        id = UUID.randomUUID(),
        key = this.key,
        value = envValue,
        encryption = this.encryption,
    )
}

fun ApplicationEnv.toSimpleResDto(): ApplicationEnvSimpleResDto =
    ApplicationEnvSimpleResDto(
        id = this.id,
        name = this.name,
        description = this.description
    )

