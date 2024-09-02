package com.dcd.server.infrastructure.global.jwt.adapter

import com.dcd.server.core.domain.auth.dto.response.TokenResDto
import com.dcd.server.core.domain.auth.model.RefreshToken
import com.dcd.server.core.domain.auth.model.Role
import com.dcd.server.core.domain.auth.spi.CommandRefreshTokenPort
import com.dcd.server.core.domain.auth.spi.JwtPort
import com.dcd.server.infrastructure.global.jwt.properties.JwtProperty
import com.dcd.server.infrastructure.global.jwt.properties.TokenTimeProperty
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.security.Key
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.List

@Component
class GenerateTokenAdapter(
    private val jwtProperty: JwtProperty,
    private val tokenTimeProperty: TokenTimeProperty,
    private val commandRefreshTokenPort: CommandRefreshTokenPort
) : JwtPort {
    object JwtPrefix{
        const val ACCESS = "access"
        const val REFRESH = "refresh"
        const val ROLE = "role"
    }

    override fun generateToken(userId: String, roles: List<Role>): TokenResDto =
        TokenResDto(
            accessToken = generatedAccessToken(userId, roles),
            refreshToken = generatedRefreshToken(userId),
            accessTokenExp = LocalDateTime.now().withNano(0).plusSeconds(tokenTimeProperty.accessTime),
            refreshTokenExp = LocalDateTime.now().withNano(0).plusSeconds(tokenTimeProperty.refreshTime)
        )

    private fun generatedAccessToken(userId: String, roles: List<Role>): String =
        Jwts.builder()
            .signWith(jwtProperty.accessSecret)
            .setHeaderParam(Header.JWT_TYPE, JwtPrefix.ACCESS)
            .setId(userId)
            .claim(JwtPrefix.ROLE, roles.map { it.name })
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + tokenTimeProperty.accessTime * 1000))
            .compact()

    private fun generatedRefreshToken(userId: String): String =
        Jwts.builder()
            .signWith(jwtProperty.refreshSecret)
            .setHeaderParam(Header.JWT_TYPE, JwtPrefix.REFRESH)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + tokenTimeProperty.refreshTime * 1000))
            .compact()
            .apply {
                commandRefreshTokenPort
                    .save(RefreshToken(userId, this, tokenTimeProperty.refreshTime))
            }

    private fun generateToken(secret: Key, jwtType: String, userId: String? = null, roles: List<Role>? = null): String =
        Jwts.builder()
            .signWith(secret)
            .setHeaderParam(Header.JWT_TYPE, jwtType)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + tokenTimeProperty.refreshTime * 1000))
            .apply {
                if (userId != null)
                    this.setId(userId)
                if (roles != null)
                    this.claim(JwtPrefix.ROLE, roles.map { it.name })
            }
            .compact()
}