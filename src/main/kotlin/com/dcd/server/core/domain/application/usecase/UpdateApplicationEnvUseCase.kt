package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.dto.request.UpdateApplicationEnvReqDto
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class UpdateApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) {
    @Lock("#applicationId+#envKey")
    fun execute(applicationId: String, envKey: String, updateApplicationEnvReqDto: UpdateApplicationEnvReqDto) {
        val application = (queryApplicationPort.findById(applicationId)
            ?: throw ApplicationNotFoundException())

        val env = application.env
        if (env.containsKey(envKey).not())
            throw ApplicationEnvNotFoundException()

        val mutableEnv = env.toMutableMap()
        mutableEnv[envKey] = updateApplicationEnvReqDto.newValue
        commandApplicationPort.save(
            application.copy(
                env = mutableEnv
            )
        )
    }

    @Lock("'labels_'+#envKey")
    fun execute(labels: List<String>, envKey: String, updateApplicationEnvReqDto: UpdateApplicationEnvReqDto) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)

        val updatedApplicationList = applicationList.mapNotNull { application ->
            val env = application.env
            if (env.containsKey(envKey).not())
                return@mapNotNull null

            val mutableEnv = env.toMutableMap()
            mutableEnv[envKey] = updateApplicationEnvReqDto.newValue
            return@mapNotNull application.copy(env = mutableEnv)
        }

        commandApplicationPort.saveAll(updatedApplicationList)
    }
}