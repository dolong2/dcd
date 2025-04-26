package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsEnvException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class AddApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort
) {
    @Lock("#id")
    fun execute(id: String, addApplicationEnvReqDto: AddApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        val env = application.env
        val applicationEnvList = addApplicationEnvReqDto.envList.map { addEnv ->
            if (env.any { it.key == addEnv.key }) throw AlreadyExistsEnvException()

            ApplicationEnv(
                key = addEnv.key,
                value = addEnv.value,
                encryption = false
            )
        }

        commandApplicationEnvPort.saveAll(applicationEnvList, application)
    }

    @Lock("#labels")
    fun execute(labels: List<String>, addApplicationEnvReqDto: AddApplicationEnvReqDto) {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        applicationList.forEach { application ->
            val env = application.env
            val applicationEnvList = addApplicationEnvReqDto.envList.map { addEnv ->
                if (env.any { it.key == addEnv.key }) throw AlreadyExistsEnvException()

                ApplicationEnv(
                    key = addEnv.key,
                    value = addEnv.value,
                    encryption = false
                )
            }

            commandApplicationEnvPort.saveAll(applicationEnvList, application)
        }
    }
}