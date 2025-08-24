package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyRunningException : BasicException(ErrorCode.APPLICATION_ALREADY_RUNNING) {
}