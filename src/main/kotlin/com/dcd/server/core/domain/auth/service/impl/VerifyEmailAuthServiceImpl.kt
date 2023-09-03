package com.dcd.server.core.domain.auth.service.impl

import com.dcd.server.core.domain.auth.exception.ExpiredCodeException
import com.dcd.server.core.domain.auth.exception.InvalidAuthCodeException
import com.dcd.server.core.domain.auth.service.VerifyEmailAuthService
import com.dcd.server.core.domain.auth.spi.CommandEmailAuthPort
import com.dcd.server.core.domain.auth.spi.QueryEmailAuthPort
import org.springframework.stereotype.Service

@Service
class VerifyEmailAuthServiceImpl(
    private val queryEmailAuthPort: QueryEmailAuthPort,
    private val commandEmailAuthPort: CommandEmailAuthPort
) : VerifyEmailAuthService {
    fun verifyCode(email: String, code: String) {
        if (!queryEmailAuthPort.existsByCodeAndEmail(email, code))
            if (queryEmailAuthPort.existsByEmail(email))
                throw InvalidAuthCodeException()
            else
                throw ExpiredCodeException() // 해당 코드가 만료됨
        commandEmailAuthPort.deleteByCode(code)
    }
}