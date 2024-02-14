package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
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
    private val getExternalPortService: GetExternalPortService
) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

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

            ApplicationType.MYSQL -> {
                dockerRunService.runApplication(application, version, externalPort)
            }

            ApplicationType.REDIS -> {
                dockerRunService.runApplication(application, version, externalPort)
            }
        }
    }
}