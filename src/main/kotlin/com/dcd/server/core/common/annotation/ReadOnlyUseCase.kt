package com.dcd.server.core.common.annotation

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true, rollbackFor = [Exception::class])
annotation class ReadOnlyUseCase()
