package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.common.file.FileContent
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.model.enums.ApplicationType
import com.dcd.server.core.domain.application.properties.ApplicationSSLProperty
import com.dcd.server.core.domain.application.service.PutSSLCertificateService
import org.springframework.stereotype.Service
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

@Service
class PutSSLCertificateServiceImpl(
    private val commandPort: CommandPort,
    private val applicationSSLProperty: ApplicationSSLProperty
) : PutSSLCertificateService {
    override fun putSSLCertificate(domain: String, externalPort: Int, application: Application) {
        val directory = applicationSSLProperty.directory + domain
        val name = application.name
        commandPort.executeShellCommand("openssl pkcs12 -export -in $directory/fullchain.pem -inkey $directory/privkey -out $name/src/main/resources/$name.p12 -name tomcat -CAfile $directory/chain.pem -caname root")
        commandPort.executeShellCommand("docker stop ${name.lowercase()}")
        when(application.applicationType) {
            ApplicationType.SPRING_BOOT -> {
                val propertyPath = "./$name/src/main/resources/"
                if (File("${propertyPath}application.yml").exists()) {
                    val propertyFile = propertyPath + "application.yml"
                    FileWriter(propertyFile, true).use { fileWriter ->
                        BufferedWriter(fileWriter).use {
                            it.write("${FileContent.getSSLYmlFileContent(name, applicationSSLProperty.password)}")
                            it.newLine()
                            it.close()
                        }
                    }
                }
                else {
                    val propertyFile = propertyPath + "application.properties"
                    FileWriter(propertyFile, true).use { fileWriter ->
                        BufferedWriter(fileWriter).use {
                            it.write("${FileContent.getSSLPropertyFileContent(name, applicationSSLProperty.password)}")
                            it.newLine()
                            it.close()
                        }
                    }
                }
                commandPort.executeShellCommand(
                    "cd ${application.name} " +
                    "&& docker run --network ${application.workspace.title.replace(' ', '_')} " +
                    "--name ${application.name.lowercase()} -d " +
                    "-p ${externalPort}:${application.port} ${application.name.lowercase()}")
            }
            else -> {}
        }
    }
}