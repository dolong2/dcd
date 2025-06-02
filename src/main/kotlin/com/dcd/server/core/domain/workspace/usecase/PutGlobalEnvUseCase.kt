package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.domain.env.model.GlobalEnv
import com.dcd.server.core.domain.env.spi.CommandGlobalEnvPort
import com.dcd.server.core.domain.env.spi.QueryGlobalEnvPort
import com.dcd.server.core.domain.workspace.dto.request.PutGlobalEnvReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.model.Workspace
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class PutGlobalEnvUseCase(
    private val queryWorkspacePort: QueryWorkspacePort,
    private val commandGlobalEnvPort: CommandGlobalEnvPort,
    private val queryGlobalEnvPort: QueryGlobalEnvPort,
    private val encryptService: EncryptService
) {
    @Lock("#workspaceId")
    fun execute(workspaceId: String, putGlobalEnvReqDto: PutGlobalEnvReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        deleteUnusedEnv(putGlobalEnvReqDto, workspace)

        val globalEnvList = putGlobalEnvReqDto.envList.map { putEnv ->
            val envValue =
                if (putEnv.encryption)
                    encryptService.encryptData(putEnv.value)
                else
                    putEnv.value

            queryGlobalEnvPort.findByKeyAndWorkspace(putEnv.key, workspace)
                ?.let {
                    GlobalEnv(
                        id = it.id,
                        key = putEnv.key,
                        value = envValue,
                        encryption = putEnv.encryption
                    )
                }
                ?: GlobalEnv(
                    key = putEnv.key,
                    value = envValue,
                    encryption = putEnv.encryption
                )
        }

        commandGlobalEnvPort.saveAll(globalEnvList, workspace)
    }

    private fun deleteUnusedEnv(
        putGlobalEnvReqDto: PutGlobalEnvReqDto,
        workspace: Workspace,
    ) {
        val putEnvKeyList = putGlobalEnvReqDto.envList.map { it.key }
        val deletedEnv = workspace.globalEnv.filterNot { it.key in putEnvKeyList }
        commandGlobalEnvPort.deleteAll(deletedEnv)
    }
}