package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.properties.ApplicationSSLProperty
import com.dcd.server.core.domain.application.service.PutSSLCertificateService
import org.springframework.stereotype.Service

@Service
class PutSSLCertificateServiceImpl(
    private val commandPort: CommandPort,
    private val applicationSSLProperty: ApplicationSSLProperty
) : PutSSLCertificateService {
    override fun putSSLCertificate(domain: String, application: Application) {
        val directory = applicationSSLProperty.directory + domain
        commandPort.executeShellCommand("openssl pkcs12 -export -in $directory/fullchain.pem -inkey $directory/privkey -out ${application.name}/src/main/resources/${application.name}.p12 -name tomcat -CAfile $directory/chain.pem -caname root")
    }
}