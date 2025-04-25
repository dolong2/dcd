package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) {
    @Lock("#id+#key")
    fun execute(id: String, key: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val updatedEnv = application.env.associate { it.key to it.value }.toMutableMap()
        updatedEnv.remove(key)
            ?: throw ApplicationEnvNotFoundException()
        commandApplicationPort.save(application.copy(env = updatedEnv.map { ApplicationEnv(key = it.key, value = it.value, encryption = false) }))
    }

    @Lock("'labels_'+#key")
    fun execute(labels: List<String>, key: String) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)
        val updatedApplicationList = applicationList.mapNotNull { application ->
            val env = application.env

            val mutableEnv = env.associate { it.key to it.value }.toMutableMap()
            mutableEnv.remove(key)
                ?: return@mapNotNull null

            return@mapNotNull application.copy(env = mutableEnv.map { ApplicationEnv(key = it.key, value = it.value, encryption = false) })
        }

        commandApplicationPort.saveAll(updatedApplicationList)
    }
}