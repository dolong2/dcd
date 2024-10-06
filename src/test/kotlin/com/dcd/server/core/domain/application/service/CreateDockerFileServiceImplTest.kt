package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.CreateDockerFileServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.common.command.adapter.CommandAdapter
import com.dcd.server.core.domain.application.spi.CheckExitValuePort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher
import util.application.ApplicationGenerator
import java.io.File

class CreateDockerFileServiceImplTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val commandPort = spyk(CommandAdapter())
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    val checkExitValuePort = mockk<CheckExitValuePort>(relaxUnitFun = true)
    val createDockerFileService = CreateDockerFileServiceImpl(queryApplicationPort, commandPort, checkExitValuePort, eventPublisher)

    given("스프링 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.SPRING_BOOT)

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리를 생성해야함") {
                verify { commandPort.executeShellCommand("mkdir ${application.name}") }
            }
            then("실제로 애플리케이션 이름의 디렉토리가 생성되야함") {
                commandPort.executeShellCommand("test -e ${application.name}") shouldBe 0
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }
                actualFileContent.deleteAt(actualFileContent.length - 1)

                actualFileContent.toString() shouldBe FileContent.getSpringBootDockerFileContent(application.name, application.version, application.port, application.env)
            }
        }

        commandPort.executeShellCommand("rm -rf ${application.name}") // 실제로 생성된 디렉토리 제거
    }

    given("레디스 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.REDIS)

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리를 생성해야함") {
                verify { commandPort.executeShellCommand("mkdir ${application.name}") }
            }
            then("실제로 애플리케이션 이름의 디렉토리가 생성되야함") {
                commandPort.executeShellCommand("test -e ${application.name}") shouldBe 0
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }

                actualFileContent.toString() shouldBe FileContent.getRedisDockerFileContent(application.version, application.port, application.env)
            }
        }

        commandPort.executeShellCommand("rm -rf ${application.name}") // 실제로 생성된 디렉토리 제거
    }

    given("MYSQL 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.MYSQL)

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리를 생성해야함") {
                verify { commandPort.executeShellCommand("mkdir ${application.name}") }
            }
            then("실제로 애플리케이션 이름의 디렉토리가 생성되야함") {
                commandPort.executeShellCommand("test -e ${application.name}") shouldBe 0
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }

                actualFileContent.toString() shouldBe FileContent.getMYSQLDockerFileContent(application.version, application.port, application.env)
            }
        }

        commandPort.executeShellCommand("rm -rf ${application.name}") // 실제로 생성된 디렉토리 제거
    }

    given("MARIADB 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.MARIA_DB)

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리를 생성해야함") {
                verify { commandPort.executeShellCommand("mkdir ${application.name}") }
            }
            then("실제로 애플리케이션 이름의 디렉토리가 생성되야함") {
                commandPort.executeShellCommand("test -e ${application.name}") shouldBe 0
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }

                actualFileContent.toString() shouldBe FileContent.getMARIADBDockerFileContent(application.version, application.port, application.env)
            }
        }

        commandPort.executeShellCommand("rm -rf ${application.name}") // 실제로 생성된 디렉토리 제거
    }

})