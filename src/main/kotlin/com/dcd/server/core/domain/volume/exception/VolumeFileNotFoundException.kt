package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class VolumeFileNotFoundException : BasicException(ErrorCode.VOLUME_FILE_NOT_FOUND)
