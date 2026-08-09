package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class VolumeUploadFailureException : BasicException(ErrorCode.FAILURE_VOLUME_UPLOAD)
