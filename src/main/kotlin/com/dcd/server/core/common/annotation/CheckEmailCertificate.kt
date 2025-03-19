package com.dcd.server.core.common.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CheckEmailCertificate(
    val target: String
)
