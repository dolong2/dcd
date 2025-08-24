package com.dcd.server.core.domain.auth.spi

import com.dcd.server.core.domain.auth.model.TokenBlackList

interface CommandTokenBlackListPort {
    fun save(tokenBlackList: TokenBlackList)
}