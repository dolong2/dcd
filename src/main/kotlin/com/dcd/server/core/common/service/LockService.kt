package com.dcd.server.core.common.service

interface LockService {
    fun <T> lock(lockKey: String, waitTime: Long, leaseTime: Long, block: () -> T): T?
}