package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class VolumeCopyFailureException : BasicException(ErrorCode.FAILURE_VOLUME_COPY)
