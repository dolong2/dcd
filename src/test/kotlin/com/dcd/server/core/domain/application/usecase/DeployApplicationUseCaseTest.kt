package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.exception.CanNotDeployApplicationException
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
import io.kotest.assertions.throwables.shouldThrow
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

                coVerify { commandPort.executeShellCommand("docker rm ${result.containerName}") }
                coVerify { commandPort.executeShellCommand("docker rmi ${result.containerName}") }
                coVerify { commandPort.executeShellCommand("git clone ${result.githubUrl} '${result.name}'") }
                coVerify { commandPort.executeShellCommand("cd ./'${result.name}' && docker build -t ${result.containerName}:latest .") }
                coVerify {
                    commandPort.executeShellCommand(
                        "docker create --network ${result.workspace.title.replace(' ', '_')} " +
                            "--name ${result.containerName} " +
                            "-p ${result.externalPort}:${result.port} ${result.containerName}:latest"
                    )
                }
                coVerify { commandPort.executeShellCommand("rm -rf '${result.name}'") }
            }
        }
    }

    given("존재하지 않는 애플리케이션의 아이디가 주어지고") {
        val notFoundApplicationId = "notFoundApplicationId"

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<ApplicationNotFoundException> {
                    deployApplicationUseCase.execute(notFoundApplicationId)
                }
            }
        }
    }

    given("이미 애플리케이션이 실행중이고") {
        val target = queryApplicationPort.findById(targetApplicationId)!!
        commandApplicationPort.save(target.copy(status = ApplicationStatus.RUNNING))

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<CanNotDeployApplicationException> {
                    deployApplicationUseCase.execute(targetApplicationId)
                }
            }
        }
    }

    given("애플리케이션이 다른 작업중이고") {
        val target = queryApplicationPort.findById(targetApplicationId)!!
        commandApplicationPort.save(target.copy(status = ApplicationStatus.PENDING))

        `when`("유스케이스를 실행할때") {

            then("에러가 발생해야함") {
                shouldThrow<CanNotDeployApplicationException> {
                    deployApplicationUseCase.execute(targetApplicationId)
                }
            }
        }
    }
})