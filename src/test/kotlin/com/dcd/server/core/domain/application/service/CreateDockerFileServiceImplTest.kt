package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.common.spi.EncryptPort
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.CreateDockerFileServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.infrastructure.global.file.adapter.FileOperationAdapter
import com.dcd.server.core.domain.application.spi.QueryApplicationInitialScriptPort
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.context.ApplicationEventPublisher
import util.application.ApplicationGenerator
import java.io.File
import java.nio.file.Paths

class CreateDockerFileServiceImplTest : BehaviorSpec({
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val queryApplicationEnvPort = mockk<QueryApplicationEnvPort>()
    val queryApplicationInitialScriptPort = mockk<QueryApplicationInitialScriptPort>()
    val fileOperationPort = FileOperationAdapter()
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    val encryptPort = mockk<EncryptPort>()
    val createDockerFileService = CreateDockerFileServiceImpl(queryApplicationPort, queryApplicationEnvPort, queryApplicationInitialScriptPort, fileOperationPort, eventPublisher, encryptPort)

    given("스프링 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.SPRING_BOOT)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리가 생성되야함") {
                Paths.get(application.name).toFile().exists() shouldBe true
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }
                actualFileContent.deleteAt(actualFileContent.length - 1)

                actualFileContent.toString() shouldBe FileContent.getApplicationDockerFileContent(application.applicationType, application.version, application.port, emptyMap(), listOf())
            }
        }

        fileOperationPort.deleteDirectory(Paths.get(application.name))
    }

    given("레디스 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.REDIS)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리가 생성되야함") {
                Paths.get(application.name).toFile().exists() shouldBe true
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }

                actualFileContent.toString() shouldBe FileContent.getApplicationDockerFileContent(application.applicationType, application.version, application.port, emptyMap(), listOf())
            }
        }

        fileOperationPort.deleteDirectory(Paths.get(application.name))
    }

    given("MYSQL 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.MYSQL)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리가 생성되야함") {
                Paths.get(application.name).toFile().exists() shouldBe true
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }

                actualFileContent.toString() shouldBe FileContent.getApplicationDockerFileContent(application.applicationType, application.version, application.port, emptyMap(), listOf())
            }
        }

        fileOperationPort.deleteDirectory(Paths.get(application.name))
    }

    given("MARIADB 애플리케이션이 주어지고") {
        val application =
            ApplicationGenerator.generateApplication(applicationType = ApplicationType.MARIA_DB)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()

        `when`("서비스를 실행할때") {
            createDockerFileService.createFileToApplication(application, application.version)

            then("애플리케이션의 이름을 가진 디렉토리가 생성되야함") {
                Paths.get(application.name).toFile().exists() shouldBe true
            }
            then("생성된 DockerFile의 내용은 FileContent의 내용과 같아야함") {
                val actualFileContent = StringBuilder()
                File("./${application.name}/Dockerfile").forEachLine {
                    actualFileContent.append(it + "\n")
                }

                actualFileContent.toString() shouldBe FileContent.getApplicationDockerFileContent(application.applicationType, application.version, application.port, emptyMap(), listOf())
            }
        }

        fileOperationPort.deleteDirectory(Paths.get(application.name))
    }

})