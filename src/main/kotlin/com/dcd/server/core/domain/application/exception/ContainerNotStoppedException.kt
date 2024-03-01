package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class ContainerNotStoppedException : BasicException(ErrorCode.CONTAINER_NOT_STOPPED) {
}