package com.dcd.server.infrastructure.global.security.auth

import com.dcd.server.core.domain.user.spi.UserPort
import com.dcd.server.infrastructure.global.jwt.exception.TokenNotValidException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class UserDetailsService(
    private val userPort: UserPort
) : UserDetailsService {
    override fun loadUserByUsername(username: String): org.springframework.security.core.userdetails.UserDetails {
        if (!userPort.exitsById(username))
            throw TokenNotValidException()
        return UserDetails(username)
    }
}