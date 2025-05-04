package com.dcd.server.core.common.spi

import com.dcd.server.core.common.annotation.Limit

interface LimitPort {
    fun consumer(key: String, limit: Limit): Boolean
}