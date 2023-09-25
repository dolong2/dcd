package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.spi.CompareUserPort
import com.dcd.server.core.domain.application.dto.request.SpringApplicationRunReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationRunOptionNotValidException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import kotlin.RuntimeException

@UseCase
class SpringApplicationRunUseCase(
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createDockerComposeFileService: CreateDockerComposeFileService,
    private val createContainerService: CreateContainerService,
    private val currentUserService: GetCurrentUserService,
    private val queryApplicationPort: QueryApplicationPort,
    private val compareUserPort: CompareUserPort
) {
    fun execute(id: String, runApplicationReqDto: SpringApplicationRunReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        if (compareUserPort.compareTwoUserEntity(currentUserService.getCurrentUser(), application.owner))
            throw RuntimeException()
        cloneApplicationByUrlService.cloneByApplication(application)
        when(application.applicationType){
            ApplicationType.SPRING_BOOT -> {
                modifyGradleService.modifyGradleByApplication(application)
                val version = runApplicationReqDto.langVersion
                val dbTypes = runApplicationReqDto.dbTypes
                val rootPassword = runApplicationReqDto.rootPassword
                    ?: throw ApplicationRunOptionNotValidException()
                val dataBaseName = runApplicationReqDto.dataBaseName
                    ?: throw ApplicationRunOptionNotValidException()
                createDockerFileService.createFileToApplication(application, version)
                buildDockerImageService.buildImageByApplication(application)
                createDockerComposeFileService.createDockerComposeYml(application, dbTypes, rootPassword, dataBaseName)
                createContainerService.createContainer(application)
            }
        }
    }
}