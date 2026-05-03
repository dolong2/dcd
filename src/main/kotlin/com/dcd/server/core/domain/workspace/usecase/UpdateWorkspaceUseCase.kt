package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.Lock
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class UpdateWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val queryApplicationPort: QueryApplicationPort,
    private val containerPort: ContainerPort
) {
    @Lock("#workspaceId")
    fun execute(workspaceId: String, updateWorkspaceReqDto: UpdateWorkspaceReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        val updatedWorkspace = workspace.copy(title = updateWorkspaceReqDto.title, description = updateWorkspaceReqDto.description)

        if (workspace.title != updateWorkspaceReqDto.title) {
            val applicationList = queryApplicationPort.findAllByWorkspace(workspace)

            containerPort.execute {
                createNetwork(updatedWorkspace)
                applicationList.forEach {
                    disconnectNetwork(workspace, it)
                    connectNetwork(updatedWorkspace, it)
                }
                deleteNetwork(workspace)
            }
        }

        commandWorkspacePort.save(updatedWorkspace)
    }
}