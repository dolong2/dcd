package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@Deprecated("사용되지 않을 유스케이스")
//@UseCase
class DeleteApplicationEnvUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryApplicationEnvPort: QueryApplicationEnvPort
) {
    @Lock("#id+#key")
    fun execute(id: String, key: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        val applicationEnv = (queryApplicationEnvPort.findByKeyAndApplication(key, application)
            ?: throw ApplicationEnvNotFoundException())

        commandApplicationEnvPort.deleteDetail(applicationEnv)
    }

    @Lock("'labels_'+#key")
    fun execute(labels: List<String>, key: String) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val envList = queryApplicationPort.findAllByWorkspace(workspace, labels)
            .flatMap { queryApplicationEnvPort.findByApplication(it) }

        commandApplicationEnvPort.deleteAll(envList)
    }
}