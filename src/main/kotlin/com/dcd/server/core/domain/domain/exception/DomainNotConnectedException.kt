package com.dcd.server.core.domain.domain.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class DomainNotConnectedException : BasicException(ErrorCode.NOT_CONNECTED_DOMAIN) {

}
