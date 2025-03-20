package com.dcd.server.core.common.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Component
@Transactional(readOnly = true, rollbackFor = [Exception::class])
annotation class ReadOnlyUseCase(
    @get:AliasFor(annotation = Transactional::class, attribute = "rollbackFor")
    val rollbackFor: Array<KClass<out Throwable>> = [Exception::class],

    @get:AliasFor(annotation = Transactional::class, attribute = "noRollbackFor")
    val noRollbackFor: Array<KClass<out Throwable>> = []
)
