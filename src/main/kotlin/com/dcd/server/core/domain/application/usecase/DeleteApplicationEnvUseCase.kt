package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val commandApplicationPort: CommandApplicationPort,
    private val workspaceInfo: WorkspaceInfo
) {
    fun execute(id: String, key: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val updatedEnv = application.env.toMutableMap()
        updatedEnv.remove(key)
            ?: throw ApplicationEnvNotFoundException()
        commandApplicationPort.save(application.copy(env = updatedEnv))
    }

    fun execute(labels: List<String>, key: String) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationList = queryApplicationPort.findAllByWorkspace(workspace, labels)
        val updatedApplicationList = applicationList.mapNotNull { application ->
            val env = application.env

            val mutableEnv = env.toMutableMap()
            mutableEnv.remove(key)
                ?: return@mapNotNull null

            return@mapNotNull application.copy(env = mutableEnv)
        }

        commandApplicationPort.saveAll(updatedApplicationList)
    }
}