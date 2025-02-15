package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.extenstion.toEntity
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.response.CreateApplicationResDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import com.dcd.server.core.domain.workspace.spi.QueryWorkspacePort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@UseCase
class CreateApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val queryWorkspacePort: QueryWorkspacePort,
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val createDockerFileService: CreateDockerFileService,
    private val getExternalPortService: GetExternalPortService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(workspaceId: String, createApplicationReqDto: CreateApplicationReqDto): CreateApplicationResDto {
        println("workspaceId = ${workspaceId}")

        val workspace = queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException()

        println("workspace = ${workspace}")

        val externalPort = getExternalPortService.getExternalPort(createApplicationReqDto.port)

        if (queryApplicationPort.existsByNameAndWorkspace(createApplicationReqDto.name, workspace))
            throw AlreadyExistsApplicationException()

        val application = createApplicationReqDto.toEntity(workspace, externalPort)
        commandApplicationPort.save(application)

        val version = application.version

        launch {
            val applicationType = application.applicationType
            when(applicationType) {
                ApplicationType.SPRING_BOOT, ApplicationType.NEST_JS -> {
                    cloneApplicationByUrlService.cloneByApplication(application)
                }
                else -> {}
            }

            createDockerFileService.createFileToApplication(application, version)
            buildDockerImageService.buildImageByApplication(application)
            createContainerService.createContainer(application, externalPort)

            deleteApplicationDirectoryService.deleteApplicationDirectory(application)
        }

        return CreateApplicationResDto(application.id)
    }
}