package com.dcd.server.core.domain.domain.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.domain.dto.extension.toEntity
import com.dcd.server.core.domain.domain.dto.request.CreateDomainReqDto
import com.dcd.server.core.domain.domain.dto.respone.CreateDomainResDto
import com.dcd.server.core.domain.domain.exception.AlreadyExistsDomainException
import com.dcd.server.core.domain.domain.spi.CommandDomainPort
import com.dcd.server.core.domain.domain.spi.QueryDomainPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class CreateDomainUseCase(
    private val commandDomainPort: CommandDomainPort,
    private val queryDomainPort: QueryDomainPort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(createDomainReqDto: CreateDomainReqDto): CreateDomainResDto {
        //도메인은 워크스페이스에 관계없이 전역적으로 관리되어야함
        if (queryDomainPort.existsByName(createDomainReqDto.name))
            throw AlreadyExistsDomainException()

        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val domain = createDomainReqDto.toEntity(workspace)
        commandDomainPort.save(domain)

        return CreateDomainResDto(domain.id)
    }
}