package com.dcd.server.core.domain.env.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.dto.extension.toModel
import com.dcd.server.core.domain.env.dto.request.PutApplicationReqDto
import com.dcd.server.core.domain.env.model.ApplicationEnvMatcher
import com.dcd.server.core.domain.env.spi.CommandApplicationEnvPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException

@UseCase
class PutApplicationEnvUseCase(
    private val workspaceInfo: WorkspaceInfo,
    private val commandApplicationEnvPort: CommandApplicationEnvPort,
    private val queryApplicationPort: QueryApplicationPort,
) {

    fun execute(putApplicationReqDto: PutApplicationReqDto) {
        val workspace = (workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException())

        val applicationEnv = putApplicationReqDto.toModel(workspace)
        commandApplicationEnvPort.save(applicationEnv)

        val applicationListByLabel = putApplicationReqDto.applicationLabelList
            ?.let { queryApplicationPort.findAllByWorkspace(workspace, it) }
            ?: emptyList()
        val applicationListByIds = (putApplicationReqDto.applicationIdList
            ?.let { queryApplicationPort.findByIds(it) }
            ?: emptyList())

        val applicationSet = (applicationListByLabel + applicationListByIds).toSet()

        val envMatcherList = applicationSet.map {
            ApplicationEnvMatcher(
                application = it,
                applicationEnv = applicationEnv
            )
        }
        commandApplicationEnvPort.saveAllMatcher(envMatcherList)
    }
}