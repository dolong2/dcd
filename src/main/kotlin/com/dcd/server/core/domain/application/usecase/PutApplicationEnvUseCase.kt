package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.domain.application.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class PutApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort,
    private val encryptService: EncryptService
) {
    @Lock("#id")
    fun execute(id: String, putApplicationEnvReqDto: PutApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        val applicationEnvList = putApplicationEnvReqDto.envList.map { putEnv ->
            val envValue =
                if (putEnv.encryption)
                    encryptService.encryptData(putEnv.value)
                else
                    putEnv.value

            queryApplicationEnvPort.findByKeyAndApplication(putEnv.key, application)
                ?.let {
                    ApplicationEnv(
                        id = it.id,
                        key = putEnv.key,
                        value = envValue,
                        encryption = putEnv.encryption
                    )
                }
                ?: ApplicationEnv(
                    key = putEnv.key,
                    value = envValue,
                    encryption = putEnv.encryption
                )
        }

        commandApplicationEnvPort.saveAll(applicationEnvList, application)
    }

    @Lock("#labels")
    fun execute(labels: List<String>, putApplicationEnvReqDto: PutApplicationEnvReqDto) {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        applicationList.forEach { application ->
            val applicationEnvList = putApplicationEnvReqDto.envList.map { putEnv ->
                val envValue =
                    if (putEnv.encryption)
                        encryptService.encryptData(putEnv.value)
                    else
                        putEnv.value

                queryApplicationEnvPort.findByKeyAndApplication(putEnv.key, application)
                    ?.let {
                        ApplicationEnv(
                            id = it.id,
                            key = putEnv.key,
                            value = envValue,
                            encryption = putEnv.encryption
                        )
                    }
                    ?: ApplicationEnv(
                        key = putEnv.key,
                        value = envValue,
                        encryption = putEnv.encryption
                    )
            }

            commandApplicationEnvPort.saveAll(applicationEnvList, application)
        }
    }
}