package com.dcd.server.core.domain.env.dto.extension

import com.dcd.server.core.domain.env.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.env.dto.request.PutEnvReqDto
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

fun PutApplicationEnvReqDto.toModel(workspace: Workspace): ApplicationEnv =
    ApplicationEnv(
        name = this.name,
        description = this.description,
        details = this.details.map { it.toModel() },
        workspace = workspace
    )

fun PutEnvReqDto.toModel(): ApplicationEnvDetail =
    ApplicationEnvDetail(
        id = UUID.randomUUID(),
        key = this.key,
        value = this.value,
        encryption = this.encryption,
    )