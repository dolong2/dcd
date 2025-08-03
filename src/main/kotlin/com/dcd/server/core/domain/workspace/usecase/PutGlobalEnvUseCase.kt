package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.model.GlobalEnvDetail
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.workspace.dto.request.PutGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class PutGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandGlobalEnvPort: CommandGlobalEnvPort,
    private val encryptService: EncryptService
) {
    @Lock("#workspaceId")
    fun execute(workspaceId: String, putGlobalEnvReqDto: PutGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        val globalEnv = GlobalEnv(
            name = putGlobalEnvReqDto.name,
            description = putGlobalEnvReqDto.description,
            workspace = workspace,
            details = listOf()
        )

        val globalEnvDetailList = putGlobalEnvReqDto.envList.map { putEnv ->
            val envValue =
                if (putEnv.encryption)
                    encryptService.encryptData(putEnv.value)
                else
                    putEnv.value

            GlobalEnvDetail(
                key = putEnv.key,
                value = envValue,
                encryption = putEnv.encryption
            )
        }

        commandGlobalEnvPort.save(globalEnv.copy(details = globalEnvDetailList), workspace)
    }
}