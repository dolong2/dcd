package com.dcd.server.core.domain.volume.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class VolumeMountNotFoundException : BasicException(ErrorCode.VOLUME_MOUNT_NOT_FOUND)
