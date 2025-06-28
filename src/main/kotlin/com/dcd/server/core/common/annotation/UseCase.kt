package com.dcd.server.core.common.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Component
@Transactional
annotation class UseCase(
    @get:AliasFor(annotation = Transactional::class, attribute = "rollbackFor")
    val rollbackFor: Array<KClass<out Throwable>> = [Exception::class],

    @get:AliasFor(annotation = Transactional::class, attribute = "noRollbackFor")
    val noRollbackFor: Array<KClass<out Throwable>> = [],

    @get:AliasFor(annotation = Transactional::class, attribute = "readOnly")
    val readOnly: Boolean = false
)
