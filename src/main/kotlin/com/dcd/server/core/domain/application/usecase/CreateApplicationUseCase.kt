package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.data.WorkspaceInfo
import com.dcd.server.core.common.spi.ContainerPort
import com.dcd.server.core.domain.application.dto.extenstion.toEntity
import com.dcd.server.core.domain.application.dto.request.CreateApplicationReqDto
import com.dcd.server.core.domain.application.dto.response.CreateApplicationResDto
import com.dcd.server.core.domain.application.exception.AlreadyExistsApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.CreateImageFileService
import com.dcd.server.core.domain.application.service.DeleteApplicationDirectoryService
import com.dcd.server.core.domain.application.service.GetExternalPortService
import com.dcd.server.core.domain.application.service.InitialScriptService
import com.dcd.server.core.domain.application.spi.ApplicationRemoteRepoPort
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.env.service.EnvAutoMatchService
import com.dcd.server.core.domain.volume.spi.QueryVolumePort
import com.dcd.server.core.domain.workspace.exception.WorkspaceNotFoundException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@UseCase
class CreateApplicationUseCase(
    private val commandApplicationPort: CommandApplicationPort,
    private val queryApplicationPort: QueryApplicationPort,
    private val workspaceInfo: WorkspaceInfo,
    private val containerPort: ContainerPort,
    private val queryVolumePort: QueryVolumePort,
    private val applicationRemoteRepoPort: ApplicationRemoteRepoPort,
    private val createImageFileService: CreateImageFileService,
    private val getExternalPortService: GetExternalPortService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService,
    private val envAutoMatchService: EnvAutoMatchService,
    private val initialScriptService: InitialScriptService
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    fun execute(createApplicationReqDto: CreateApplicationReqDto): CreateApplicationResDto {
        val workspace = workspaceInfo.workspace
            ?: throw WorkspaceNotFoundException()

        val externalPort = getExternalPortService.getExternalPort(createApplicationReqDto.port)

        if (queryApplicationPort.existsByNameAndWorkspace(createApplicationReqDto.name, workspace))
            throw AlreadyExistsApplicationException()

        val application = createApplicationReqDto.toEntity(workspace, externalPort)
        commandApplicationPort.save(application)

        val version = application.version

        envAutoMatchService.match(workspace, application)
        initialScriptService.write(application, createApplicationReqDto.initialScripts)

        launch {
            val applicationType = application.applicationType
            when(applicationType) {
                ApplicationType.SPRING_BOOT, ApplicationType.NEST_JS -> {
                    applicationRemoteRepoPort.cloneApplicationRemoteRepo(application)
                }
                else -> {}
            }

            createImageFileService.createFileToApplication(application)

            containerPort.execute {
                buildImage(application, "./${application.name}/Dockerfile")
                val volumeMounts = queryVolumePort.findAllMountByApplication(application)
                createContainer(application, volumeMounts)
            }

            deleteApplicationDirectoryService.deleteApplicationDirectory(application)
        }

        return CreateApplicationResDto(application.id)
    }
}