package com.dcd.server.infrastructure.domain.application.adapter

import com.dcd.server.core.common.file.spi.FileOperationPort
import com.dcd.server.core.common.spi.EncryptPort
import com.dcd.server.core.domain.application.model.ApplicationInitialScript
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.spi.QueryApplicationInitialScriptPort
import com.dcd.server.core.domain.env.model.ApplicationEnv
import com.dcd.server.core.domain.env.model.ApplicationEnvDetail
import com.dcd.server.core.domain.env.spi.QueryApplicationEnvPort
import com.dcd.server.infrastructure.domain.application.file.ImageFileContent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationEventPublisher
import util.application.ApplicationGenerator
import java.nio.file.Paths
import java.util.UUID

class ApplicationImageFileAdapterTest : BehaviorSpec({
    val queryApplicationEnvPort = mockk<QueryApplicationEnvPort>()
    val queryApplicationInitialScriptPort = mockk<QueryApplicationInitialScriptPort>()
    val fileOperationPort = mockk<FileOperationPort>()
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    val encryptPort = mockk<EncryptPort>()
    val adapter = ApplicationImageFileAdapter(
        queryApplicationEnvPort,
        queryApplicationInitialScriptPort,
        fileOperationPort,
        eventPublisher,
        encryptPort
    )

    given("SPRING_BOOT 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.SPRING_BOOT)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 SPRING_BOOT용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("NEST_JS 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.NEST_JS)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 NEST_JS용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("GIN 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.GIN)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 GIN용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("MYSQL 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.MYSQL)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 MYSQL용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("MARIA_DB 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.MARIA_DB)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 MARIA_DB용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("REDIS 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.REDIS)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 REDIS용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("H2_DB 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.H2_DB)
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일 내용이 H2_DB용으로 생성되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("환경변수가 있는 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.SPRING_BOOT)
        val envDetails = listOf(
            ApplicationEnvDetail(id = UUID.randomUUID(), key = "DATABASE_URL", value = "jdbc:mysql://localhost:3306/db", encryption = false),
            ApplicationEnvDetail(id = UUID.randomUUID(), key = "API_KEY", value = "encrypted_value", encryption = true)
        )
        val appEnv = ApplicationEnv(
            id = UUID.randomUUID(),
            name = "testEnv",
            description = "test environment",
            details = envDetails,
            workspace = application.workspace,
            labels = emptyList()
        )
        every { queryApplicationEnvPort.findByApplication(application) } returns listOf(appEnv)
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns emptyList()
        every { encryptPort.decrypt("encrypted_value") } returns "decrypted_value"
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일에 환경변수가 포함되어야함") {
                val env = mapOf(
                    "DATABASE_URL" to "jdbc:mysql://localhost:3306/db",
                    "API_KEY" to "decrypted_value"
                )
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    env,
                    listOf()
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

    given("초기 스크립트가 있는 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.SPRING_BOOT)
        val initialScripts = listOf(
            "apt-get update && apt-get install -y curl",
            "mkdir -p /app/logs"
        )
        val scriptModels = initialScripts.map { script ->
            ApplicationInitialScript(
                id = UUID.randomUUID(),
                script = script,
                application = application
            )
        }
        every { queryApplicationEnvPort.findByApplication(application) } returns emptyList()
        every { queryApplicationInitialScriptPort.findAllByApplication(application) } returns scriptModels
        every { fileOperationPort.createDirectory(any()) } returns Unit
        every { fileOperationPort.writeFile(any(), any()) } returns Unit

        `when`("이미지 파일을 생성할때") {
            runBlocking {
                adapter.createImageFile(application)
            }

            then("도커파일에 초기 스크립트가 포함되어야함") {
                val expectedContent = ImageFileContent.getApplicationDockerFileContent(
                    application.applicationType,
                    application.version,
                    application.port,
                    emptyMap(),
                    initialScripts
                )

                verify {
                    fileOperationPort.writeFile(
                        Paths.get(application.directoryName, "Dockerfile"),
                        expectedContent
                    )
                }
            }
        }
    }

})
