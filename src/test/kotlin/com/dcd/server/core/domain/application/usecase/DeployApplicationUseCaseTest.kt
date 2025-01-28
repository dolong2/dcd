package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.service.*
import com.dcd.server.core.domain.application.spi.CommandApplicationPort
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.spi.CommandUserPort
import com.dcd.server.core.domain.workspace.spi.CommandWorkspacePort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coVerify
import com.dcd.server.infrastructure.test.application.ApplicationGenerator
import com.dcd.server.infrastructure.test.user.UserGenerator
import com.dcd.server.infrastructure.test.workspace.WorkspaceGenerator
import com.ninjasquad.springmockk.MockkBean
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class DeployApplicationUseCaseTest(
    private val deployApplicationUseCase: DeployApplicationUseCase,
    @MockkBean(relaxed = true)
    private val commandPort: CommandPort,
    @MockkBean(relaxUnitFun = true)
    private val modifyGradleService: ModifyGradleService,
    @MockkBean(relaxUnitFun = true)
    private val createDockerFileService: CreateDockerFileService,
    private val commandUserPort: CommandUserPort,
    private val commandWorkspacePort: CommandWorkspacePort,
    private val commandApplicationPort: CommandApplicationPort,
    private val queryApplicationPort: QueryApplicationPort
) : BehaviorSpec({
    val targetApplicationId = "testApplicationId"

    beforeSpec {
        val user = UserGenerator.generateUser()
        val workspace = WorkspaceGenerator.generateWorkspace(user = user)
        val application = ApplicationGenerator.generateApplication(id = targetApplicationId, workspace = workspace)

        commandUserPort.save(user)
        commandWorkspacePort.save(workspace)
        commandApplicationPort.save(application)
    }

    given("애플리케이션 id가 주어지고") {

        `when`("주어진 id의 애플리케이션이 spring boot 타입의 애플리케이션일때") {
            deployApplicationUseCase.execute(targetApplicationId)

            then("애플리케이션이 보류 상태로 바뀌고 여러 배포 커맨드를 실행해야함") {
                val result = queryApplicationPort.findById(targetApplicationId)

                result shouldNotBe null
                result!!.status shouldBe ApplicationStatus.PENDING

                coVerify { commandPort.executeShellCommand("docker rm ${result.name.lowercase()}") }
                coVerify { commandPort.executeShellCommand("docker rmi ${result.name.lowercase()}") }
                coVerify { commandPort.executeShellCommand("git clone ${result.githubUrl} ${result.name}") }
                coVerify { commandPort.executeShellCommand("cd ./${result.name} && docker build -t ${result.name.lowercase()}:latest .") }
                coVerify {
                    commandPort.executeShellCommand(
                        "docker create --network ${result.workspace.title.replace(' ', '_')} " +
                            "--name ${result.name.lowercase()} " +
                            "-p ${result.externalPort}:${result.port} ${result.name.lowercase()}:latest"
                    )
                }
                coVerify { commandPort.executeShellCommand("rm -rf ${result.name}") }
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
                coVerify { deleteContainerService.deleteContainer(application) }
                coVerify { deleteImageService.deleteImage(application) }
            }
            then("도커파일을 생성하고, 이미지를 빌드하고, 컨테이너를 생성해야함") {
                coVerify { createDockerFileService.createFileToApplication(application, application.version) }
                coVerify { buildDockerImageService.buildImageByApplication(application) }
                coVerify { createContainerService.createContainer(application, application.externalPort) }
            }
            then("생성된 애플리케이션 디렉토리를 제거해야함") {
                coVerify { deleteApplicationDirectoryService.deleteApplicationDirectory(application) }
            }
        }

        `when`("주어진 id의 애플리케이션이 REDIS 타입의 애플리케이션일때") {
            val application = ApplicationGenerator.generateApplication(
                id = applicationId,
                applicationType = ApplicationType.REDIS
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            deployApplicationUseCase.execute(applicationId)

            then("이미지와 컨테이너를 삭제하는 서비스를 실행해야함") {
                coVerify { deleteContainerService.deleteContainer(application) }
                coVerify { deleteImageService.deleteImage(application) }
            }
            then("도커파일을 생성하고, 이미지를 빌드하고, 컨테이너를 생성해야함") {
                coVerify { createDockerFileService.createFileToApplication(application, application.version) }
                coVerify { buildDockerImageService.buildImageByApplication(application) }
                coVerify { createContainerService.createContainer(application, application.externalPort) }
            }
            then("생성된 애플리케이션 디렉토리를 제거해야함") {
                coVerify { deleteApplicationDirectoryService.deleteApplicationDirectory(application) }
            }
        }

        `when`("주어진 id의 애플리케이션이 실행중인 애플리케이션일때") {
            val application = ApplicationGenerator.generateApplication(
                id = applicationId,
                status = ApplicationStatus.RUNNING
            )
            every { queryApplicationPort.findById(applicationId) } returns application

            then("CanNotDeployApplicationException이 발생해야함") {
                shouldThrow<CanNotDeployApplicationException> {
                    deployApplicationUseCase.execute(applicationId)
                }
            }
        }

        `when`("주어진 id를 가진 애플리케이션이 존재하지 않을때") {
            every { queryApplicationPort.findById(applicationId) } returns null

            then("ApplicationNotFoundException이 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deployApplicationUseCase.execute(applicationId)
                }
            }
        }
    }
})