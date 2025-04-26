package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class UpdateApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort
) {
    @Lock("#applicationId+#envKey")
    fun execute(applicationId: String, envKey: String, updateApplicationEnvReqDto: UpdateApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        val env = application.env
        val applicationEnv = (env.find { it.key == envKey }
            ?: throw ApplicationEnvNotFoundException())

        applicationEnv.value = updateApplicationEnvReqDto.newValue
        commandApplicationEnvPort.save(applicationEnv, application)
    }

    @Lock("'labels_'+#envKey")
    fun execute(labels: List<String>, envKey: String, updateApplicationEnvReqDto: UpdateApplicationEnvReqDto) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationEnvList = queryApplicationPort.findAllByWorkspace(workspace, labels)
            .associateWith { it.env }
            .filter { it.value.any { env -> env.key == envKey } }

        applicationEnvList.forEach {
            val application = it.key

            it.value.forEach { env ->
                env.value = updateApplicationEnvReqDto.newValue
            }

            commandApplicationEnvPort.saveAll(it.value, application)
        }
    }
}