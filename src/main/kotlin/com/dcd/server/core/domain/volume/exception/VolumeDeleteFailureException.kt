package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class VolumeDeleteFailureException : BasicException(ErrorCode.FAILURE_VOLUME_DELETE)
