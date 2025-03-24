package com.dcd.server.core.common.annotation

import com.dcd.server.core.domain.auth.model.enums.EmailAuthUsage

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class CheckEmailCertificate(
    val target: String,
    val usage: EmailAuthUsage
)
