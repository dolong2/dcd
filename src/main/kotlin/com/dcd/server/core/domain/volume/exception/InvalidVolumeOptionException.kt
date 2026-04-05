package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.exception.BasicException
import com.dcd.server.core.common.exception.ErrorCode

class InvalidVolumeOptionException : BasicException(ErrorCode.INVALID_VOLUME_OPTION)
