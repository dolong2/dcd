package com.dcd.server.core.domain.application.service

import com.dcd.server.core.domain.application.model.Application

interface PutSSLCertificateService {
    fun putSSLCertificate(domain: String, externalPort: Int, application: Application)
}