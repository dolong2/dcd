package com.dcd.server.core.common.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Lock(
    val lockName: String = "", // Lock 이름
    val waitTime: Long = 1000L, // 대기 시간
    val leaseTime: Long = 1000L // 락 점유 시간
)
