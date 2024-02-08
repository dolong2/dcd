package com.dcd.server.core.domain.application.service

interface GenerateSSLCertificateService {

    fun generateSSL(domain: String)

    fun generateSSL(domain: String, email: String)
}