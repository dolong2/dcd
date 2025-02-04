package com.dcd.server.core.domain.workspace.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.workspace.dto.request.UpdateWorkspaceReqDto
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.*
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class UpdateWorkspaceUseCase(
    private val commandWorkspacePort: CommandWorkspacePort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val deleteNetworkService: DeleteNetworkService,
    private val disconnectNetworkService: DisconnectNetworkService,
    private val createNetworkService: CreateNetworkService,
    private val connectNetworkService: ConnectNetworkService
) {
    fun execute(workspaceId: String, updateWorkspaceReqDto: UpdateWorkspaceReqDto) {
        val workspace = (queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException())

        validateWorkspaceOwnerService.validateOwner(workspace)

        val updatedWorkspace = workspace.copy(title = updateWorkspaceReqDto.title, description = updateWorkspaceReqDto.description)

        if (workspace.title != updateWorkspaceReqDto.title) {
            //기존 워크스페이스에서 생성된 네트워크 분리
            disconnectNetworkService.disconnectNetwork(workspace)
            //기존 워크스페이스의 네트워크 삭제
            deleteNetworkService.deleteNetwork(workspace.networkName)
            //수정된 네트워크 생성
            createNetworkService.createNetwork(updatedWorkspace.networkName)
            //워크스페이스의 애플리케이션에 신규 생성된 네트워크 연결
            connectNetworkService.connectNetworkByWorkspace(updatedWorkspace)
        }

        commandWorkspacePort.save(updatedWorkspace)
    }
}