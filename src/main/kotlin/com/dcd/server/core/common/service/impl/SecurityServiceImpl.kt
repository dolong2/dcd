package com.dcd.server.core.common.service.impl

import com.dcd.server.core.common.service.SecurityService
import com.dcd.server.core.common.service.exception.PasswordNotCorrectException
import com.dcd.server.core.common.spi.SecurityPort
import org.springframework.stereotype.Service

@Service
class SecurityServiceImpl(
    private val securityPort: SecurityPort
) : SecurityService{
    override fun getCurrentUserId(): String =
        securityPort.getCurrentUserId()

    override fun encodePassword(rawPassword: String): String =
        securityPort.encodeRawPassword(rawPassword)

    override fun matchPassword(rawPassword: String, encodedPassword: String) {
        if(securityPort.isCorrectPassword(rawPassword, encodedPassword).not())
            throw PasswordNotCorrectException()
    }

}