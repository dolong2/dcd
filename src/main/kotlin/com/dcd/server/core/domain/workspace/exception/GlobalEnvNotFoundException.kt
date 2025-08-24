package com.dcd.server.core.domain.workspace.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class GlobalEnvNotFoundException : BasicException(ErrorCode.GLOBAL_ENV_NOT_FOUND) {
}