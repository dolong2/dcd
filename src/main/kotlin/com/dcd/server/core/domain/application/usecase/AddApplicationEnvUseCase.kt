package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.request.AddApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsEnvException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class AddApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) {
    @Lock("#id")
    fun execute(id: String, addApplicationEnvReqDto: AddApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val envMutable = application.env.toMutableMap()
        addApplicationEnvReqDto.envList.forEach {
            if (envMutable.containsKey(it.key)) throw AlreadyExistsEnvException()

            envMutable[it.key] = it.value
        }
        commandApplicationPort.save(application.copy(env = envMutable))
    }

    @Lock("#labels")
    fun execute(labels: List<String>, addApplicationEnvReqDto: AddApplicationEnvReqDto) {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()
        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        val updatedApplicationList = applicationList.map { application ->
            val envMutable = application.env.toMutableMap()
            addApplicationEnvReqDto.envList.forEach {
                if (envMutable.containsKey(it.key)) return@forEach

                envMutable[it.key] = it.value
            }
            application.copy(env = envMutable)
        }

        commandApplicationPort.saveAll(updatedApplicationList)
    }
}