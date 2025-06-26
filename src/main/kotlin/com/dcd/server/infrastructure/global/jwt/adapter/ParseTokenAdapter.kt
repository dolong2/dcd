package com.dcd.server.infrastructure.global.jwt.adapter

import com.dcd.server.core.domain.auth.spi.ParseTokenPort
import com.dcd.server.core.domain.auth.spi.QueryTokenBlackListPort
import com.dcd.server.infrastructure.global.jwt.exception.*
import com.dcd.server.infrastructure.global.jwt.properties.JwtProperty
import com.dcd.server.infrastructure.global.security.auth.AuthDetailsService
import io.jsonwebtoken.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key

@Component
class ParseTokenAdapter(
    private val jwtProperty: JwtProperty,
    private val authDetailsService: AuthDetailsService,
    private val queryTokenBlackListPort: QueryTokenBlackListPort
) : ParseTokenPort {
    override fun parseToken(token: String): String? =
        if(token.startsWith(JwtPrefix.PREFIX))
            token.substring(JwtPrefix.PREFIX.length)
        else null

    override fun getJwtType(token: String): String {
        val claims = getClaims(token, jwtProperty.refreshSecret)

        return claims.header[Header.JWT_TYPE] as? String ?: ""
    }

    override fun getUserId(token: String): String =
        getAuthentication(token).name

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token, jwtProperty.accessSecret)

        if(claims.header[Header.JWT_TYPE] != JwtPrefix.ACCESS)
            throw TokenTypeNotValidException()

        val userDetails = getDetails(claims.body)
        if (userDetails.isAccountNonLocked.not())
            throw AccountLockedException()
        else if (userDetails.isEnabled.not())
            throw AccountNotEnabledException()

        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }


    private fun getClaims(token: String, secret: Key): Jws<Claims> {
        if (queryTokenBlackListPort.existsByToken(token))
            throw ExpiredTokenException()

        return try {
            Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
        } catch (e: Exception) {
            when(e) {
                is InvalidClaimException -> throw TokenNotValidException()
                is ExpiredJwtException -> throw ExpiredTokenException()
                is JwtException -> throw TokenNotValidException()
                else -> throw TokenNotValidException()
            }
        }
    }

    private fun getDetails(body: Claims): UserDetails {
        val username = body.id

        return authDetailsService.loadUserByUsername(username)
    }
}