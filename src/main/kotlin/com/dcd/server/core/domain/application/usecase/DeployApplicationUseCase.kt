package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.annotation.ApplicationOwnerVerification
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeployApplicationException
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
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
    @ApplicationOwnerVerification
    fun execute(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())

        if (application.status == ApplicationStatus.RUNNING)
            throw CanNotDeployApplicationException()

        deleteContainerService.deleteContainer(application)
        deleteImageService.deleteImage(application)

        val version = application.version
        val externalPort = application.externalPort

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
}