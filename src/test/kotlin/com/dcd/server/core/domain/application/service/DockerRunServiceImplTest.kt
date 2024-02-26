package com.dcd.server.core.domain.application.service

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationStatus
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.impl.DockerRunServiceImpl
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import util.application.ApplicationGenerator
import java.util.*

class DockerRunServiceImplTest : BehaviorSpec({
    val commandPort = mockk<CommandPort>(relaxed = true)
    val queryApplicationPort = mockk<QueryApplicationPort>()
    val service = DockerRunServiceImpl(queryApplicationPort, commandPort)

    given("애플리케이션id가 주어지고") {
        val appId = UUID.randomUUID().toString()

        `when`("executeShellCommand를 실행할때") {
            val application = ApplicationGenerator.generateApplication()
            every { queryApplicationPort.findById(appId) } returns application

            service.runApplication(appId, application.port)
            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify { commandPort.executeShellCommand("cd ${application.name} && docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} ${application.name.lowercase()}:latest") }
            }
        }
    }

    given("애플리케이션이 주이지고") {
        val application = ApplicationGenerator.generateApplication()

        `when`("executeShellCommand 메서드를 실행할때") {
            service.runApplication(application, application.port)

            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify { commandPort.executeShellCommand("cd ${application.name} && docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} ${application.name.lowercase()}:latest") }
            }
        }
    }

    given("mysql 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.MYSQL, env = mapOf(Pair("rootPassword", "test"), Pair("database", "test")))

        `when`("executeShellCommand 메서드를 실행할때") {
            service.runApplication(application, application.port)

            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify {
                    commandPort.executeShellCommand("docker run --network ${application.workspace.title.replace(' ', '_')} -e MYSQL_ROOT_PASSWORD=${application.env["rootPassword"]} -e MYSQL_DATABASE=${application.env["database"]} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} mysql:latest")
                }
            }
        }
    }

    given("mariadb 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.MARIA_DB, env = mapOf(Pair("rootPassword", "test"), Pair("database", "test")))

        `when`("executeShellCommand 메서드를 실행할때") {
            service.runApplication(application, application.port)

            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify {
                    commandPort.executeShellCommand("docker run --network ${application.workspace.title.replace(' ', '_')} -e MYSQL_ROOT_PASSWORD=${application.env["rootPassword"]} -e MYSQL_DATABASE=${application.env["database"]} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} mariadb:latest")
                }
            }
        }
    }

    given("redis 애플리케이션이 주어지고") {
        val application = ApplicationGenerator.generateApplication(applicationType = ApplicationType.REDIS)

        `when`("executeShellCommand 메서드를 실행할때") {
            service.runApplication(application, application.port)

            then("commandPort가 실행되어야함") {
                val externalPort = application.port
                verify {
                    commandPort.executeShellCommand("docker run --network ${application.workspace.title.replace(' ', '_')} --name ${application.name.lowercase()} -d -p ${externalPort}:${application.port} redis:latest")
                }
            }
        }
    }
})