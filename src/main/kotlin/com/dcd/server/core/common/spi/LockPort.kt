package com.dcd.server.core.common.spi

interface LockPort {
    fun <T> lock(lockKey: String, waitTime: Long, leaseTime: Long, block: () -> T): T?
}
