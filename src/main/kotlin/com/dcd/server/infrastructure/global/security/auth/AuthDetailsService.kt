package com.dcd.server.infrastructure.global.security.auth

import com.dcd.server.core.domain.user.spi.QueryUserPort
import com.dcd.server.infrastructure.global.jwt.exception.TokenNotValidException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component

@Component
class AuthDetailsService(
    private val queryUserPort: QueryUserPort
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = (queryUserPort.findById(username)
            ?: throw TokenNotValidException())
        return AuthDetails(user)
    }
}