package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.domain.application.dto.request.RunApplicationReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.workspace.service.ValidateWorkspaceOwnerService

@ReadOnlyUseCase
class ApplicationRunUseCase(
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val dockerRunService: DockerRunService,
    private val queryApplicationPort: QueryApplicationPort,
    private val validateWorkspaceOwnerService: ValidateWorkspaceOwnerService
) {
    fun execute(id: String, runApplicationReqDto: RunApplicationReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        validateWorkspaceOwnerService.validateOwner(application.workspace)

        when(application.applicationType){

            ApplicationType.SPRING_BOOT -> {
                cloneApplicationByUrlService.cloneByApplication(application)
                modifyGradleService.modifyGradleByApplication(application)
                val version = runApplicationReqDto.version
                createDockerFileService.createFileToApplication(application, version)
                buildDockerImageService.buildImageByApplication(application)
                dockerRunService.runApplication(application)
            }

            ApplicationType.MYSQL -> {
                dockerRunService.runApplication(application, runApplicationReqDto.version)
            }

            ApplicationType.REDIS -> {
                dockerRunService.runApplication(application, runApplicationReqDto.version)
            }
        }
    }
}