package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.exception.ApplicationEnvNotFoundException
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.service.DockerRunService
import com.dcd.server.core.domain.application.service.ExistsPortService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import org.springframework.stereotype.Service

@Service
class DockerRunServiceImpl(
    private val queryApplicationPort: QueryApplicationPort,
    private val existsPortService: ExistsPortService,
    private val commandPort: CommandPort
) : DockerRunService {
    override fun runApplication(id: String, externalPort: Int) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        run(application, externalPort = externalPort)
    }

    override fun runApplication(application: Application, externalPort: Int) {
        run(application, externalPort = externalPort)
    }

    override fun runApplication(id: String, version: String, externalPort: Int) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        run(application, version, externalPort)
    }

    override fun runApplication(application: Application, version: String, externalPort: Int) {
        run(application, version, externalPort)
    }

    private fun run(application: Application, version: String = "latest", externalPort: Int) {

        when (application.applicationType) {
            ApplicationType.SPRING_BOOT -> {
                commandPort.executeShellCommand(
                    "cd ${application.name} " +
                    "&& docker run --network ${application.workspace.title.replace(' ', '_')} " +
                    "--name ${application.name.lowercase()} -d " +
                    "-p ${externalPort}:${application.port} ${application.name.lowercase()}:$version"
                )
            }

            ApplicationType.MYSQL -> {
                commandPort.executeShellCommand(
                    "docker run --network ${application.workspace.title.replace(' ', '_')} " +
                    "-e MYSQL_ROOT_PASSWORD=${application.env["rootPassword"] ?: throw ApplicationEnvNotFoundException()} " +
                    "--name ${application.name.lowercase()} -d " +
                    "-p ${externalPort}:${application.port} mysql:$version"
                )
            }

            ApplicationType.REDIS -> {
                commandPort.executeShellCommand(
                    "docker run --network ${application.workspace.title.replace(' ', '_')} " +
                    "--name ${application.name.lowercase()} -d " +
                    "-p ${externalPort}:${application.port} redis:$version"
                )
            }
        }
    }
}