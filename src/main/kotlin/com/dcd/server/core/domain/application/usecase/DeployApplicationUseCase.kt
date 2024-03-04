package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort

@UseCase
class DeployApplicationUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val deleteContainerService: DeleteContainerService,
    private val deleteImageService: DeleteImageService,
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService,
    private val deleteApplicationDirectoryService: DeleteApplicationDirectoryService
) {
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        deleteContainerService.deleteContainer(application)
        deleteImageService.deleteImage(application)

        val version = application.version
        val externalPort = application.externalPort

        if (application.applicationType == ApplicationType.SPRING_BOOT) {
            cloneApplicationByUrlService.cloneByApplication(application)
            modifyGradleService.modifyGradleByApplication(application)
        }

        createDockerFileService.createFileToApplication(application, version)
        buildDockerImageService.buildImageByApplication(application)
        createContainerService.createContainer(application, externalPort)

        deleteApplicationDirectoryService.deleteApplicationDirectory(application)
    }
}