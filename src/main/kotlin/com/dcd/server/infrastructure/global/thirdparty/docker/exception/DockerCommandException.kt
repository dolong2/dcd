package com.dcd.server.infrastructure.global.thirdparty.docker.exception

import com.dcd.server.core.domain.application.model.Application
import com.dcd.server.core.domain.application.util.FailureCase

class DockerCommandException(val application: Application, val failureCase: FailureCase, message: String?) : RuntimeException(message)