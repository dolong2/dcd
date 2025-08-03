package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.service.EncryptService
import com.dcd.server.core.domain.application.dto.request.PutApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import java.util.UUID

@UseCase
class PutApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val encryptService: EncryptService
) {
    @Lock("#id")
    fun execute(id: String, putApplicationEnvReqDto: PutApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        var applicationEnv = ApplicationEnv(
            id = UUID.randomUUID(),
            name = putApplicationEnvReqDto.name,
            description = putApplicationEnvReqDto.description,
            details = listOf()
        )

        val applicationEnvDetailList = putApplicationEnvReqDto.envList.map { putEnv ->
            val envValue =
                if (putEnv.encryption)
                    encryptService.encryptData(putEnv.value)
                else
                    putEnv.value

            ApplicationEnvDetail(
                id = UUID.randomUUID(),
                key = putEnv.key,
                value = envValue,
                encryption = putEnv.encryption
            )
        }

        applicationEnv = applicationEnv.copy(details = applicationEnvDetailList)

        commandApplicationEnvPort.save(applicationEnv, application)
    }

    @Lock("#labels")
    fun execute(labels: List<String>, putApplicationEnvReqDto: PutApplicationEnvReqDto) {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        applicationList.forEach { application ->

            var applicationEnv = ApplicationEnv(
                id = UUID.randomUUID(),
                name = putApplicationEnvReqDto.name,
                description = putApplicationEnvReqDto.description,
                details = listOf()
            )

            val applicationEnvDetailList = putApplicationEnvReqDto.envList.map { putEnv ->
                val envValue =
                    if (putEnv.encryption)
                        encryptService.encryptData(putEnv.value)
                    else
                        putEnv.value

                ApplicationEnvDetail(
                    id = UUID.randomUUID(),
                    key = putEnv.key,
                    value = envValue,
                    encryption = putEnv.encryption
                )
            }

            applicationEnv = applicationEnv.copy(details = applicationEnvDetailList)

            commandApplicationEnvPort.save(applicationEnv, application)
        }
    }
}