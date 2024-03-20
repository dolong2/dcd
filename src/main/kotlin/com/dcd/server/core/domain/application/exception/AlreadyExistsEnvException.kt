package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyExistsEnvException : BasicException(ErrorCode.ALREADY_EXISTS_APPLICATION_ENV) {
}