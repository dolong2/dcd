package com.dcd.server.core.domain.domain.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyConnectedDomainException : BasicException(ErrorCode.ALREADY_CONNECTED_DOMAIN) {

}
