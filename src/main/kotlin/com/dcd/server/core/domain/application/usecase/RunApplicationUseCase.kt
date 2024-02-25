package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.response.RunApplicationResDto
import com.dcd.server.core.domain.application.exception.AlreadyRunningException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@ReadOnlyUseCase
class RunApplicationUseCase(
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val dockerRunService: DockerRunService,
    private val queryApplicationPort: QueryApplicationPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService,
    private val getExternalPortService: GetExternalPortService,
    private val changeApplicationStatusService: ChangeApplicationStatusService
) {
    fun execute(id: String): RunApplicationResDto {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw AlreadyRunningException()

        validateWorkspaceOwnerService.validateOwner(application.workspace)

        val version = application.version
        val externalPort = getExternalPortService.getExternalPort(application.port)
        when(application.applicationType){
            ApplicationType.SPRING_BOOT -> {
                cloneApplicationByUrlService.cloneByApplication(application)
                modifyGradleService.modifyGradleByApplication(application)
                createDockerFileService.createFileToApplication(application, version, externalPort)
                buildDockerImageService.buildImageByApplication(application)
                dockerRunService.runApplication(application, externalPort)
            }

            else -> {
                dockerRunService.runApplication(application, version, externalPort)
            }
        }

        changeApplicationStatusService.changeApplicationStatus(application, ApplicationStatus.RUNNING)

        return RunApplicationResDto(externalPort = externalPort)
    }
}