package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class InvalidVolumeFilePathException : BasicException(ErrorCode.INVALID_VOLUME_FILE_PATH)
