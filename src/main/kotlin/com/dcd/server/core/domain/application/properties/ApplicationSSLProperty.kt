package com.dcd.server.core.domain.application.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application.ssl")
class ApplicationSSLProperty(
    val directory: String
)