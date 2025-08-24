package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyStoppedException : BasicException(ErrorCode.APPLICATION_ALREADY_STOPPED) {
}