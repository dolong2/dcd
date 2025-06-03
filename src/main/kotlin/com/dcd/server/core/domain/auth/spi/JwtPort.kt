package com.dcd.server.core.domain.auth.spi

import org.springframework.stereotype.Component

@Component
interface JwtPort : GenerateTokenPort, ParseTokenPort