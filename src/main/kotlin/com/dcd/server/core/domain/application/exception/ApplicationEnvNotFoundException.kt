package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class ApplicationEnvNotFoundException : BasicException(ErrorCode.APPLICATION_ENV_NOT_FOUND) {
}