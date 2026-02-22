package com.dcd.server.infrastructure.domain.application.exception

import com.dcd.server.core.common.error.BasicException
import com.dcd.server.core.common.error.ErrorCode

class DockerHubRateLimitExceededException : BasicException(ErrorCode.IMAGE_REGISTRY_RATE_LIMIT_EXCEEDED) {
}