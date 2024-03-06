package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator

class DeployApplicationUseCaseTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val deleteContainerService = mockk<DeleteContainerService>(relaxUnitFun = true)
    val deleteImageService = mockk<DeleteImageService>(relaxUnitFun = true)
    val cloneApplicationByUrlService = mockk<CloneApplicationByUrlService>(relaxUnitFun = true)
    val modifyGradleService = mockk<ModifyGradleService>(relaxUnitFun = true)
    val createDockerFileService = mockk<CreateDockerFileService>(relaxUnitFun = true)
    val buildDockerImageService = mockk<BuildDockerImageService>(relaxUnitFun = true)
    val createContainerService = mockk<CreateContainerService>(relaxUnitFun = true)
    val deleteApplicationDirectoryService = mockk<DeleteApplicationDirectoryService>(relaxUnitFun = true)
    val deployApplicationUseCase = DeployApplicationUseCase(
        queryApplicationPort,
        deleteContainerService,
        deleteImageService,
        cloneApplicationByUrlService,
        modifyGradleService,
        createDockerFileService,
        buildDockerImageService,
        createContainerService,
        deleteApplicationDirectoryService
    )

    given("애플리케이션 id가 주어지고") {
        val applicationId = "testApplicationId"

        `when`("주어진 id의 애플리케이션이 spring boot 타입의 애플리케이션일때") {
            val application = ApplicationGenerator.generateApplication(
                id = applicationId,
                applicationType = ApplicationType.SPRING_BOOT
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            deployApplicationUseCase.execute(applicationId)

            then("이미지와 컨테이너를 삭제하는 서비스를 실행해야함") {
                verify { deleteContainerService.deleteContainer(application) }
                verify { deleteImageService.deleteImage(application) }
            }
            then("애플리케이션을 클론하고, 그래들 파일을 수정해야함") {
                verify { cloneApplicationByUrlService.cloneByApplication(application) }
                verify { modifyGradleService.modifyGradleByApplication(application) }
            }
            then("도커파일을 생성하고, 이미지를 빌드하고, 컨테이너를 생성해야함") {
                verify { createDockerFileService.createFileToApplication(application, application.version) }
                verify { buildDockerImageService.buildImageByApplication(application) }
                verify { createContainerService.createContainer(application, application.externalPort) }
            }
            then("생성된 애플리케이션 디렉토리를 제거해야함") {
                verify { deleteApplicationDirectoryService.deleteApplicationDirectory(application) }
            }
        }

        `when`("주어진 id의 애플리케이션이 MYSQL 타입의 애플리케이션일때") {
            val application = ApplicationGenerator.generateApplication(
                id = applicationId,
                applicationType = ApplicationType.MYSQL
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            deployApplicationUseCase.execute(applicationId)

            then("이미지와 컨테이너를 삭제하는 서비스를 실행해야함") {
                verify { deleteContainerService.deleteContainer(application) }
                verify { deleteImageService.deleteImage(application) }
            }
            then("도커파일을 생성하고, 이미지를 빌드하고, 컨테이너를 생성해야함") {
                verify { createDockerFileService.createFileToApplication(application, application.version) }
                verify { buildDockerImageService.buildImageByApplication(application) }
                verify { createContainerService.createContainer(application, application.externalPort) }
            }
            then("생성된 애플리케이션 디렉토리를 제거해야함") {
                verify { deleteApplicationDirectoryService.deleteApplicationDirectory(application) }
            }
        }
    }
})