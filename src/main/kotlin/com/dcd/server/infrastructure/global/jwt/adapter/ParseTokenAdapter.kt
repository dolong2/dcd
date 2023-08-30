package com.dcd.server.infrastructure.global.jwt.adapter

import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.infrastructure.global.jwt.exception.ExpiredTokenException
import com.dcd.server.infrastructure.global.jwt.properties.JwtProperty
import com.dcd.server.infrastructure.global.security.auth.AdminDetailsService
import com.dcd.server.infrastructure.global.security.auth.UserDetailsService
import com.dcd.server.infrastructure.global.jwt.exception.TokenNotValidException
import com.dcd.server.infrastructure.global.security.auth.DeveloperDetailsService
import io.jsonwebtoken.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key


@Component
class ParseTokenAdapter(
    private val jwtProperty: JwtProperty,
    private val userDetailsService: UserDetailsService,
    private val adminDetailsService: AdminDetailsService,
    private val developerDetailsService: DeveloperDetailsService
) {
    object JwtPrefix{
        const val ACCESS = "access"
        const val REFRESH = "refresh"
        const val ROLE = "role"
        const val PREFIX = "Bearer "
    }
    fun parseToken(token: String): String? =
        if(token.startsWith(JwtPrefix.PREFIX)) token.substring(JwtPrefix.PREFIX.length) else null
    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token, jwtProperty.accessSecret)

        if(claims.header[Header.JWT_TYPE] != JwtPrefix.ACCESS)
            throw RuntimeException()

        val userDetails = getDetails(claims.body)

        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    fun getClaimsBody(token: String, secret: Key): String {
        val claims = getClaims(token, secret)
        return claims.body.id
    }

    private fun getClaims(token: String, secret: Key): Jws<Claims> {
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
                else -> throw RuntimeException()
            }
        }
    }

    private fun getDetails(body: Claims): UserDetails {
        val role = body[JwtPrefix.ROLE, String::class.java]

        return when(role) {
            Role.ROLE_USER.name -> userDetailsService.loadUserByUsername(body.id)
            Role.ROLE_ADMIN.name -> adminDetailsService.loadUserByUsername(body.id)
            Role.ROLE_DEVELOPER.name -> developerDetailsService.loadUserByUsername(body.id)
            else -> throw throw RuntimeException()
        }
    }
}