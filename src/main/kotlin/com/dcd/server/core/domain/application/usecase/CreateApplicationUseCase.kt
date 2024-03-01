package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toEntity
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort

@UseCase
class CreateApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val getExternalPortService: GetExternalPortService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService
) {
    fun execute(workspaceId: String, createApplicationReqDto: CreateApplicationReqDto) {
        val workspace = queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException()
        validateWorkspaceOwnerService.validateOwner(workspace)
        val application = createApplicationReqDto.toEntity(workspace)
        commandApplicationPort.save(application)

        val version = application.version
        val externalPort = getExternalPortService.getExternalPort(application.port)
        when(application.applicationType){
            ApplicationType.SPRING_BOOT -> {
                cloneApplicationByUrlService.cloneByApplication(application)
                modifyGradleService.modifyGradleByApplication(application)
                createDockerFileService.createFileToApplication(application, version, externalPort)
                buildDockerImageService.buildImageByApplication(application)
                createContainerService.createContainer(application, externalPort)
            }

            else -> {
                createContainerService.createContainer(application, version, externalPort)
            }
        }
    }
}