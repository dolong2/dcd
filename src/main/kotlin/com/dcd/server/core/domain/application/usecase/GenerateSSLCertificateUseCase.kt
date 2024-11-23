package com.dcd.server.core.domain.application.usecase

import com.dcd.server.core.common.annotation.UseCase
import com.dcd.server.core.domain.application.dto.request.GenerateSSLCertificateReqDto
import com.dcd.server.core.domain.application.exception.ApplicationNotFoundException
import com.dcd.server.core.domain.application.service.GenerateHttpConfigService
import com.dcd.server.core.domain.application.service.GenerateSSLCertificateService
import com.dcd.server.core.domain.application.service.GetExternalPortService
import com.dcd.server.core.domain.application.service.PutSSLCertificateService
import com.dcd.server.core.domain.application.spi.QueryApplicationPort
import com.dcd.server.core.domain.user.service.GetCurrentUserService
import com.dcd.server.core.domain.workspace.exception.WorkspaceOwnerNotSameException

@UseCase
class GenerateSSLCertificateUseCase(
    private val queryApplicationPort: QueryApplicationPort,
    private val getCurrentUserService: GetCurrentUserService,
    private val generateSSLCertificateService: GenerateSSLCertificateService,
    private val putSSLCertificateService: PutSSLCertificateService,
    private val getExternalPortService: GetExternalPortService,
    private val generateHttpConfigService: GenerateHttpConfigService
) {
    fun execute(id: String, generateSSLCertificateReqDto: GenerateSSLCertificateReqDto) {
        val application = (queryApplicationPort.findById(id)
            ?: throw ApplicationNotFoundException())
        val currentUser = getCurrentUserService.getCurrentUser()
        if (currentUser.id != application.workspace.owner.id)
            throw WorkspaceOwnerNotSameException()
        val domain = generateSSLCertificateReqDto.domain
        val externalPort = getExternalPortService.getExternalPort(application.port)
        generateHttpConfigService.generateWebServerConfig(application, domain)
        generateSSLCertificateService.generateSSL(domain)
        putSSLCertificateService.putSSLCertificate(domain, externalPort, application)
    }
}