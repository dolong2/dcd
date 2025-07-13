package com.dcd.server.core.domain.domain.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class DomainNotFoundException : BasicException(ErrorCode.DOMAIN_NOT_FOUND) {
}