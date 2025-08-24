package com.dcd.server.core.domain.domain.dto.extension

import com.dcd.server.core.domain.application.dto.extenstion.toProfileDto
import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.core.domain.domain.dto.response.DomainResDto
import com.dcd.server.core.domain.domain.model.Domain
import com.dcd.server.core.domain.workspace.model.Workspace
import java.util.UUID

fun CreateDomainReqDto.toEntity(workspace: Workspace): Domain =
    Domain(
        id = UUID.randomUUID().toString(),
        name = this.name,
        description = this.description,
        application = null,
        workspace = workspace,
    )

fun Domain.toResDto(): DomainResDto =
    DomainResDto(
        id = this.id,
        name = this.name,
        description = this.description,
        application = this.application?.toProfileDto()
    )