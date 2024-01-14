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
    override fun runApplication(id: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        run(application)
    }

    override fun runApplication(application: Application) {
        run(application)
    }

    override fun runApplication(id: String, version: String) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        run(application, version)
    }

    override fun runApplication(application: Application, version: String) {
        run(application, version)
    }

    private fun run(application: Application, version: String = "latest") {
        when (application.applicationType) {
            ApplicationType.SPRING_BOOT -> {
                var externalPort = application.port
                while (existsPortService.existsPort(externalPort)) {
                    externalPort += 1
                }
                commandPort.executeShellCommand(
                    "cd ${application.name} " +
                            "&& docker run --network ${application.workspace.title.replace(' ', '_')} " +
                            "--name ${application.name.lowercase()} -d " +
                            "-p ${externalPort}:${application.port} ${application.name.lowercase()}:$version"
                )
            }

            ApplicationType.MYSQL -> {
                var externalPort = application.port
                while (existsPortService.existsPort(externalPort)) {
                    externalPort += 1
                }
                commandPort.executeShellCommand(
                    "docker run --network ${application.workspace.title.replace(' ', '_')} " +
                            "-e MYSQL_ROOT_PASSWORD=${application.env["rootPassword"] ?: throw ApplicationEnvNotFoundException()} " +
                            "--name ${application.name.lowercase()} -d " +
                            "-p ${externalPort}:${application.port} mysql:$version"
                )
            }
        }
    }
}