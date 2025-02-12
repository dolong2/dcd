package com.dcd.server.core.domain.auth.spi

interface QueryTokenBlackListPort {
    fun existsByToken(token: String): Boolean
}