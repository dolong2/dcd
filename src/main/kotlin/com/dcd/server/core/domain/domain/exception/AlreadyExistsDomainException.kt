package com.dcd.server.core.domain.domain.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class AlreadyExistsDomainException : BasicException(ErrorCode.ALREADY_EXISTS_DOMAIN) {

}
