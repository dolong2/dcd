package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.ReadOnlyUseCase
import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.common.spi.CompareUserPort
import com.dcd.server.core.domain.application.dto.request.RunApplicationReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.OwnerNotSameException
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService

@ReadOnlyUseCase
class SpringApplicationRunUseCase(
    private val cloneApplicationByUrlService: CloneApplicationByUrlService,
    private val modifyGradleService: ModifyGradleService,
    private val createDockerFileService: CreateDockerFileService,
    private val buildDockerImageService: BuildDockerImageService,
    private val createContainerService: CreateContainerService,
    private val currentUserService: GetCurrentUserService,
    private val queryApplicationPort: QueryApplicationPort,
    private val compareUserPort: CompareUserPort
) {
    fun execute(id: String, runApplicationReqDto: RunApplicationReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        if (compareUserPort.compareTwoUserEntity(currentUserService.getCurrentUser(), application.workspace.owner))
            throw OwnerNotSameException()
        cloneApplicationByUrlService.cloneByApplication(application)
        when(application.applicationType){
            ApplicationType.SPRING_BOOT -> {
                modifyGradleService.modifyGradleByApplication(application)
                val version = runApplicationReqDto.langVersion
                createDockerFileService.createFileToApplication(application, version)
                buildDockerImageService.buildImageByApplication(application)
                createContainerService.createContainer(application, runApplicationReqDto.env)
            }
        }
    }
}