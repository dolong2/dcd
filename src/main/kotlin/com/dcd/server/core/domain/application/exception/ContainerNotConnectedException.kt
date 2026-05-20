package com.dcd.server.core.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class ContainerNotConnectedException : BasicException(ErrorCode.CONTAINER_NOT_CONNECTED) {
}
