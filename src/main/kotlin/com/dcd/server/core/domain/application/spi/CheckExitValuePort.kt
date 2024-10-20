package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import kotlinx.coroutines.CoroutineScope

interface CheckExitValuePort {
    fun checkApplicationExitValue(exitValue: Int, application: Application)

    fun checkApplicationExitValue(exitValue: Int, application: Application, coroutineScope: CoroutineScope)
}