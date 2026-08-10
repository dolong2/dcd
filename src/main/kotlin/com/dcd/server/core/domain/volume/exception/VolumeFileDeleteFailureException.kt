package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class VolumeFileDeleteFailureException : BasicException(ErrorCode.FAILURE_VOLUME_FILE_DELETE)
