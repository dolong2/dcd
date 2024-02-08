package com.dcd.server.core.domain.application.service.impl

import com.dcd.server.core.common.command.CommandPort
import com.dcd.server.core.domain.application.service.GenerateSSLCertificateService
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import org.springframework.stereotype.Service

@Service
class GenerateSSLCertificateServiceImpl(
    private val commandPort: CommandPort,
    private val getCurrentUserService: GetCurrentUserService
) : GenerateSSLCertificateService {
    override fun generateSSL(domain: String) {
        val user = getCurrentUserService.getCurrentUser()
        commandPort.executeShellCommand(
            "certbot certonly " +
            "--webroot " +
            "--webroot-path=/var/www/certbot" +
            "--email ${user.email} " +
            "--agree-tos " +
            "--no-eff-email " +
            "-d $domain"
        )
    }

    override fun generateSSL(domain: String, email: String) {
        commandPort.executeShellCommand(
            "certbot certonly " +
            "--webroot " +
            "--webroot-path=/var/www/certbot" +
            "--email ${email} " +
            "--agree-tos " +
            "--no-eff-email " +
            "-d $domain"
        )
    }
}