package com.dcd.server.core.domain.application.spi

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.util.FailureCase
import kotlinx.coroutines.CoroutineScope

interface CheckExitValuePort {
    fun checkApplicationExitValue(exitValue: Int, application: Application, failureCase: FailureCase?)

    fun checkApplicationExitValue(exitValue: Int, application: Application, coroutineScope: CoroutineScope, failureCase: FailureCase?)
}