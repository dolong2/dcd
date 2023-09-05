package com.dcd.server.core.common.aop.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class NotCertificateEmailException : BasicException(ErrorCode.NOT_CERTIFICATE_MAIL) {
}