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
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val getExternalPortService: GetExternalPortService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(workspaceId: String, createApplicationReqDto: CreateApplicationReqDto): CreateApplicationResDto {
        val workspace = queryWorkspacePort.findById(workspaceId)
            ?: throw WorkspaceNotFoundException()

        val externalPort = getExternalPortService.getExternalPort(createApplicationReqDto.port)

        if (queryApplicationPort.existsByName(createApplicationReqDto.name))
            throw AlreadyExistsApplicationException()

        val application = createApplicationReqDto.toEntity(workspace, externalPort)
        commandApplicationPort.save(application)

        val version = application.version

        launch {
            val applicationType = application.applicationType
            if (applicationType == ApplicationType.SPRING_BOOT) {
                cloneApplicationByUrlService.cloneByApplication(application)
                modifyGradleService.modifyGradleByApplication(application)
            }
            else if (applicationType == ApplicationType.NEST_JS) {
                cloneApplicationByUrlService.cloneByApplication(application)
            }

            createDockerFileService.createFileToApplication(application, version)
            buildDockerImageService.buildImageByApplication(application)
            createContainerService.createContainer(application, externalPort)

            deleteApplicationDirectoryService.deleteApplicationDirectory(application)
        }

        return CreateApplicationResDto(application.id)
    }
}