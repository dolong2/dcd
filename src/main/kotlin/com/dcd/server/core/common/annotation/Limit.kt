package com.dcd.server.core.common.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Limit(
    val target: String, // 키값으로 사용할 식별값의 표현식
    val capacity: Long = 10L,
    val refillAmount: Long = 1L,
    val refillDurationMinute: Long = 1L
)
